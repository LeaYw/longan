package com.foryou.library

import com.android.SdkConstants
import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import org.gradle.api.Project

import org.apache.commons.io.FileUtils
import org.apache.commons.codec.digest.DigestUtils

import java.util.jar.JarEntry
import java.util.jar.JarFile

class InjectTransform extends Transform {
    def NAME_LOGISTICS_CENTER = "com.foryou.longan_api.core.LogisticsCenter"
    def NAME_LOGISTICS_CENTER_CLASS = "com/foryou/longan_api/core/LogisticsCenter.class"
    def NAME_APP_PROXY = "com.foryou.longan_api.AppProxy"
    def NAME_APPLICATION = "android.app.Application"
    def NAME_INIT_METHOD = "init"
    Project project
    ClassPool classPool

    InjectTransform(Project project) {
        this.project = project
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        def startTime = System.currentTimeSeconds()
        classPool = new ClassPool()
//        project.android.bootClassPath.each {
//            classPool.appendClassPath(it.absolutePath.toString())
//        }
        classPool.importPackage(NAME_APPLICATION)
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
            //对类型为jar文件的input进行遍历
            input.jarInputs.each { JarInput jarInput ->
                //jar文件一般是第三方依赖库jar文件
                // 重命名输出文件（同目录copyFile会冲突）
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

                //生成输出路径

                //将输入内容复制到输出
                FileUtils.copyFile(src, dest)
            }

            //对类型为“文件夹”的input进行遍历
            input.directoryInputs.each { DirectoryInput directoryInput ->
                def dest = transformInvocation.outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes,
                        directoryInput.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(directoryInput.file, dest)
            }
        }
        println "---------------------------------------------------------------"
        injectCodeToLogisticsCenter(logisticsCenter, appProxies, path)
        println "---------------------------------------------------------------"
        def endTime = System.currentTimeSeconds()
        println "inject transform time" + (endTime - startTime)
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
                println "127" + ctClass.name
                list.add(ctClass)
            }
        } catch (Exception e) {

        }
    }

    private void injectCodeToLogisticsCenter(CtClass ctLogisticsCenter, List<CtClass> appProxies, String path) {
        println "inject logistics center start"
        println path
        ctLogisticsCenter.defrost()
//            CtClass[] param = new CtClass[1]
//            param[0] = classPool.get(NAME_APPLICATION)
        CtMethod initMethod = ctLogisticsCenter.getDeclaredMethod(NAME_INIT_METHOD)
        initMethod.insertAfter(getAutoLoadComCode(appProxies))
        def bytecode = ctLogisticsCenter.toBytecode()
        ctLogisticsCenter.detach()
        ConvertUtils.writeToJar(path, bytecode, NAME_LOGISTICS_CENTER_CLASS)
        println "inject logistics center end"
    }

    private String getAutoLoadComCode(List<CtClass> appProxies) {
        StringBuilder autoLoadComCode = new StringBuilder()
        appProxies.each {
            println "Inject AppProxy:" + it.name
            autoLoadComCode.append("new " + it.name + "()" + ".onCreate();")
        }
        println "------------autoLoadComCode: " + autoLoadComCode.toString()
        return autoLoadComCode.toString()
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