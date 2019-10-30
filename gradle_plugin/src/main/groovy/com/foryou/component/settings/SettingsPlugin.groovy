package com.foryou.component.settings


import com.foryou.component.Constant
import com.foryou.component.bean.Project
import com.foryou.component.XmlUtils
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class SettingsPlugin implements Plugin<Settings> {

    @Override
    void apply(Settings settings) {

        def debuggingRecordFile = new File("$Constant.DEBUG_PACKAGE$File.separator$Constant.DEBUG_MODULE_RECODE_FILE")
        def debuggingProjects = XmlUtils.parseDebuggingProjects(debuggingRecordFile)

        println debuggingProjects.target
        if (debuggingProjects.target) {
            include(settings, debuggingProjects.target)
            debuggingProjects.dependencies.each {
                include(settings, it)
            }
        }
    }

    private static include(Settings settings, Project it) {
        println it
        settings.include(":$it.name:$it.build_project")
    }

}