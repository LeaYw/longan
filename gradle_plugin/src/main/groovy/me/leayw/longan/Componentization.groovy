package me.leayw.longan

import org.gradle.api.Plugin
import org.gradle.api.Project

class Componentization implements Plugin<Project> {
    static final int ASSEMBLE_TYPE_GENERATE = 0
    static final int ASSEMBLE_TYPE_DEBUG = 1
    static final int ASSEMBLE_TYPE_RELEASE = 2

    @Override
    void apply(Project target) {
        def propertyFile = target.file("gradle.properties")

        if (!propertyFile.exists()) {
            propertyFile.parentFile.mkdir()
            propertyFile.text = "isDependent=false\ncompileProject=\napplicationId="//暂时先添加project
        }
        def mainModuleName = target.rootProject.mainModuleName

        if (target.name != mainModuleName) {
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

        def isDependent = Boolean.parseBoolean(target.isDependent)
        if (target.name == mainModuleName) {
            isDependent = true
            target.setProperty("isDependent", true)
        }

        if (isDependent) {
            target.apply plugin: 'com.android.application'
            target.android.defaultConfig {
                versionCode target.rootProject.properties.versionCode.toInteger()
                versionName target.rootProject.properties.versionName
            }
//            target.android.buildTypes {
//                perform {
//                    initWith debug
//                    debuggable false
//                }
//            }

            if (target.name != mainModuleName) {
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
                        applicationId target.properties.applicationId
                    } else {
                        applicationId 'com.foryou.' + target.name
                    }
                }
                target.android {
                    resourcePrefix target.name + '_'
                }
            }
            def assembleType = assembleType(target.gradle.startParameter.getTaskNames())
            if (assembleType != ASSEMBLE_TYPE_GENERATE) {
                compileDependentProject(target, "compileProject")
                initIApplication(target)
            }
        } else {
            target.apply plugin: 'com.android.library'
            target.android {
                resourcePrefix target.name + '_'
            }
            target.android.defaultConfig {
                versionCode target.rootProject.properties.versionCode.toInteger()
                versionName target.rootProject.properties.versionName
            }
//            target.android.buildTypes {
//                perform {
//                    initWith debug
//                    debuggable false
//                }
//            }
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
            if (isMaven(it)) {
                project.dependencies.add("implementation", it)
            } else {
                project.dependencies.add("implementation", project.project(":$it"))
            }
        }
    }

    static def isMaven(String project) {
        project.contains(".") && project.contains(":")
    }

    static def initIApplication(Project project) {
        project.android.registerTransform(new InjectTransform(project))
    }

    static int assembleType(List<String> taskNames) {
        def isAssemble = ASSEMBLE_TYPE_GENERATE
        if (!taskNames.isEmpty()) {
            def lastTaskName = taskNames.first()
            if (lastTaskName.contains("assembleDebug")) {
                isAssemble = ASSEMBLE_TYPE_DEBUG
            } else if (lastTaskName.contains("assembleRelease")) {
                isAssemble = ASSEMBLE_TYPE_RELEASE
            }
        }
        isAssemble
    }
}