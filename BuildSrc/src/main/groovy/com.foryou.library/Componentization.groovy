package com.foryou.library

import org.gradle.api.Plugin
import org.gradle.api.Project

class Componentization implements Plugin<Project> {
    static final int ASSEMBLE_TYPE_SYNC = 0
    static final int ASSEMBLE_TYPE_DEBUG = 1
    static final int ASSEMBLE_TYPE_RELEASE = 2

    @Override
    void apply(Project target) {
        def propertyFile = target.file("gradle.properties")

        if (!propertyFile.exists()) {
            propertyFile.parentFile.mkdir()
            propertyFile.text = "isDependent=false\ncompileProject=\napplicationId="//暂时先添加project
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

                target.android.defaultConfig {
                    if (target.properties.hasProperty("applicationId")) {
                        applicationId target.properties.get("applicationId")
                    } else {
                        applicationId 'com.foryou.' + target.getName()
                    }
                }
                target.android {
                    resourcePrefix target.getName() + '_'
                }
            }
            target.android.defaultConfig {
                versionCode target.rootProject.properties.get("versionCode").toInteger()
                versionName target.rootProject.properties.get("versionName")
            }
            def assembleType = assembleType(target.gradle.startParameter.getTaskNames())
            if (assembleType != ASSEMBLE_TYPE_SYNC) {
                compileDependentProject(target, "compileProject")
                initIApplication(target)
//                if (assembleType == ASSEMBLE_TYPE_DEBUG) {
//                    compileDependentProject(target, "debugCompileProject")
//                }
            }
        } else {
            target.apply plugin: 'com.android.library'
            target.android {
                resourcePrefix target.getName() + '_'
            }
        }
    }

    /**
     * 将依赖组件动态引入，强制解耦合
     * @param project
     */
    static void compileDependentProject(Project project, String key) {
        String allProject = project.properties.get(key)
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

    static int assembleType(List<String> taskNames) {
        def isAssemble = ASSEMBLE_TYPE_SYNC
        if (!taskNames.isEmpty()) {
            def lastTaskName = taskNames.first()
            if (lastTaskName.contains("assembleDebug")) {
                isAssemble = ASSEMBLE_TYPE_DEBUG
            } else if (lastTaskName.contains("assembleRelease")) {
                isAssemble = ASSEMBLE_TYPE_RELEASE
            }
        }
        isAssemble
//        taskNames.each {
//            if (it.toUpperCase().contains("ASSEMBLE")
//                    || it.contains("aR")
//                    || it.contains("asR")
//                    || it.contains("asD")
//                    || it.toUpperCase().contains("INSTALL")
//            ) {
//                if (it.contains("Debug")){
//                    isAssemble = ASSEMBLE_TYPE_DEBUG
//                }
//            }
//        }
//        isAssemble
    }
}