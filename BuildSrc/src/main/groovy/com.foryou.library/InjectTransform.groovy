package com.foryou.library

import com.android.SdkConstants
import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import javassist.ClassPool
import javassist.CtClass
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

import java.util.jar.JarEntry
import java.util.jar.JarFile

class InjectTransform extends Transform {
    def NAME_LOGISTICS_CENTER = "com.foryou.longan_api.core.LogisticsCenter"
    def NAME_LOGISTICS_CENTER_CLASS = "com/foryou/longan_api/core/LogisticsCenter.class"
    def NAME_APP_PROXY = "com.foryou.longan_api.AppProxy"
    def NAME_METHOD_INJECT = "inject"
    Project project
    ClassPool classPool

    InjectTransform(Project project) {
        this.project = project
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        classPool = new ClassPool()
        classPool = ClassPool.default

        def box = ConvertUtils.toCtClasses(transformInvocation.getInputs(), classPool)

        String path

        CtClass logisticsCenter
        ArrayList<CtClass> appProxies = new ArrayList<>()
        box.each {
            if (logisticsCenter == null && it.name == NAME_LOGISTICS_CENTER) {
                logisticsCenter = it
            }
            addProxy(appProxies, it)
        }

        transformInvocation.inputs.each { TransformInput input ->
            input.jarInputs.each { JarInput jarInput ->
                def jarName = jarInput.name
                def src = jarInput.file
                def md5Name = DigestUtils.md5Hex(src.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }

                def dest = transformInvocation.outputProvider.getContentLocation(jarName + "_" + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)

                def jarFile = new JarFile(src)

                Enumeration<JarEntry> classes = jarFile.entries()
                while (classes.hasMoreElements()) {
                    JarEntry libClass = classes.nextElement()
                    String className = libClass.getName()
                    if (className.endsWith(SdkConstants.DOT_CLASS)) {
                        className = className.substring(0, className.length() - SdkConstants.DOT_CLASS.length()).replaceAll('/', '.')
                        if (className == NAME_LOGISTICS_CENTER) {
                            path = dest.absolutePath
                        }
                    }
                }
                jarFile.close()

                FileUtils.copyFile(src, dest)
            }

            input.directoryInputs.each { DirectoryInput directoryInput ->
                def dest = transformInvocation.outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes,
                        directoryInput.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(directoryInput.file, dest)
            }
        }
        injectCodeToLogisticsCenter(logisticsCenter, appProxies, path)
    }

    void addProxy(ArrayList<CtClass> list, CtClass ctClass) {
        try {
            if (ctClass.interface) {
                return
            }
            boolean add = false
            ctClass.interfaces.each {
                if (it.name == NAME_APP_PROXY) {
                    add = true
                }
            }
            if (add) {
                list.add(ctClass)
            }
        } catch (Exception e) {

        }
    }

    private void injectCodeToLogisticsCenter(CtClass ctLogisticsCenter, List<CtClass> appProxies, String path) {
        ctLogisticsCenter.defrost()
        def method = ctLogisticsCenter.getDeclaredMethod(NAME_METHOD_INJECT)
        method.insertAfter(getBody(appProxies))
        def bytecode = ctLogisticsCenter.toBytecode()
        ctLogisticsCenter.detach()
        ConvertUtils.writeToJar(path, bytecode, NAME_LOGISTICS_CENTER_CLASS)
    }

    private String getBody(List<CtClass> appProxies) {
        StringBuilder builder = new StringBuilder()
        appProxies.each {
            builder.append("register(new $it.name());")
        }
        return builder.toString()
    }

    @Override
    String getName() {
        return "InjectTransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }
}