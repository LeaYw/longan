package com.foryou.component.project

import com.foryou.component.Constant
import org.gradle.api.Plugin
import org.gradle.api.Project

class ProjectPlugin implements Plugin<Project> {
    static final int ASSEMBLE_TYPE_GENERATE = 0
    static final int ASSEMBLE_TYPE_DEBUG = 1
    static final int ASSEMBLE_TYPE_RELEASE = 2

    @Override
    void apply(Project target) {
        def mainModuleName = "app"
        if (target.rootProject.hasProperty("mainModuleName")) {
            mainModuleName = target.rootProject.mainModuleName
        }

        def isApp = target.name == mainModuleName

        if (isApp) {
            target.apply plugin: 'com.android.application'

            target.extensions.create("fyComponent", FYComponentExtension)
            target.task(Constant.TASK_NAME_DEBUG_START, type: StartDebugTask)
            target.task(Constant.TASK_NAME_DEBUG_STOP, type: StopDebugTask)

            def assembleType = assembleType(target.gradle.startParameter.getTaskNames())
            if (assembleType != ASSEMBLE_TYPE_GENERATE) {
                compileDependentProject(target)
                initIApplication(target)
            }
        } else {
            target.apply plugin: 'com.android.library'
        }

        target.android.defaultConfig {
            versionCode target.rootProject.properties.versionCode.toInteger()
            versionName target.rootProject.properties.versionName
        }

        if (target.name != mainModuleName) {
            target.android {
                resourcePrefix target.name + '_'
            }
        }
    }

    /**
     * 将依赖组件动态引入，强制解耦合
     * @param project
     */
    static void compileDependentProject(Project project) {
        project.afterEvaluate {
            def fyComponent = project.extensions.fyComponent
            project.dependencies(fyComponent.dynamicDependencies)
        }
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