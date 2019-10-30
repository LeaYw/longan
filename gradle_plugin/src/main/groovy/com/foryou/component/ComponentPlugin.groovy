package com.foryou.component

import com.foryou.component.project.ProjectPlugin
import com.foryou.component.settings.SettingsPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings

class ComponentPlugin implements Plugin<Object> {

    @Override
    void apply(Object object) {
        Plugin plugin = null
        if (object instanceof Settings){
            plugin = new SettingsPlugin()
        } else if (object instanceof Project){
            plugin = new ProjectPlugin()
        }
        if (plugin != null){
            plugin.apply(object)
        }
    }

}