package com.foryou.library

import org.gradle.api.Plugin
import org.gradle.api.Project

class Componentization implements Plugin<Project> {

    @Override
    void apply(Project target) {
        def propertyFile = target.file("gradle.properties")

        if (!propertyFile.exists()) {
            propertyFile.parentFile.mkdir()
            propertyFile.text = "isDependent=false\ncompileProject="//暂时先添加project
        }
        def mainModuleName = target.rootProject.properties.get("mainModuleName")

        if (target.getName() != mainModuleName) {
            def manifest = target.file("src/main/debug/AndroidManifest.xml")
            if (!manifest.exists()) {
                manifest.parentFile.mkdirs()
                def srcManifest = target.file("src/main/AndroidManifest.xml")
                if (srcManifest.exists()) {
                    manifest << srcManifest.text
                }
            }
            def debugRes = target.file('src/main/debug/res')
            if (!debugRes.exists()) {
                debugRes.mkdir()
            }
            def debugJava = target.file('src/main/debug/java')
            if (!debugJava.exists()) {
                debugJava.mkdir()
            }
        }

        def isDependent = Boolean.parseBoolean(target.properties.get("isDependent"))//从外部文件读取，例如properties文件

        if (target.getName() == mainModuleName) {
            isDependent = true
            target.setProperty("isDependent", true)
        }

        if (isDependent) {
            target.apply plugin: 'com.android.application'
            if (target.getName() != mainModuleName) {
                target.android.sourceSets {
                    main {
                        manifest.srcFile 'src/main/debug/AndroidManifest.xml'
                        java.srcDirs = ['src/main/java', 'src/main/debug/java']
                        res.srcDirs = ['src/main/res', 'src/main/debug/res']
                        assets.srcDirs = ['src/main/assets', 'src/main/debug/assets']
                        jniLibs.srcDirs = ['src/main/jniLibs', 'src/main/debug/jniLibs']
                    }
                }
            }
            if (isAssemble(target.gradle.startParameter.getTaskNames())) {
                compileDependentProject(target)
                initIApplication(target)
            }
        } else {
            target.apply plugin: 'com.android.library'
        }
    }

    /**
     * 将依赖组件动态引入，强制解耦合
     * @param project
     */
    static void compileDependentProject(Project project) {
        String allProject = project.properties.get("compileProject")
        if (allProject == null || allProject.length() == 0) {
            return
        }
        def compileProjects = allProject.split(",")
        if (compileProjects == null || compileProjects.length == 0) {
            return
        }
        compileProjects.each {
            project.dependencies.add("implementation", project.project(":$it"))
        }
    }

    static def initIApplication(Project project) {
        //todo 实现application初始化代码时初始化组件代码，解除初始化代码耦合
    }

    static boolean isAssemble(List<String> taskNames) {
        def isAssemble = false
        taskNames.each {
            if (it.toUpperCase().contains("ASSEMBLE")
                    || it.contains("aR")
                    || it.contains("asR")
                    || it.contains("asD")
                    || it.toUpperCase().contains("INSTALL")
            ) {
                isAssemble = true
            }
        }
        isAssemble
    }
}