package me.leayw.longan

import com.android.SdkConstants
import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import javassist.ClassPool
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

class InjectTransform extends Transform {
    private Project project
    ClassPool classPool
    CtClassContainer container = new CtClassContainer()

    InjectTransform(Project project) {
        this.project = project
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)

        classPool = ClassPool.default
        transformInvocation.inputs.each { TransformInput input ->
            input.jarInputs.each { JarInput jarInput ->
                def jarName = jarInput.name
                def src = jarInput.file
                def md5Name = DigestUtils.md5Hex(src.absolutePath)
                if (jarName.endsWith(SdkConstants.DOT_JAR)) {
                    jarName = jarName.substring(0, jarName.length() - SdkConstants.DOT_JAR.length())
                }

                def dest = transformInvocation.outputProvider.getContentLocation(jarName + "_" + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)

                ScanUtil.scanJar(container, classPool, src, dest)
                FileUtils.copyFile(src, dest)
            }

            input.directoryInputs.each { DirectoryInput directoryInput ->
                classPool.insertClassPath(directoryInput.file.absolutePath)
                def dest = transformInvocation.outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes,
                        directoryInput.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(directoryInput.file, dest)
            }
        }
        ScanUtil.initAppProxy(container, classPool)
        if (container.logisticsCenter) {
            InjectCodeGenerator.generate(container)
        }
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