package com.foryou.component.project


import com.foryou.component.Constant
import com.foryou.component.XmlUtils
import com.foryou.component.bean.Project
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class StopDebugTask extends DefaultTask {

    @TaskAction
    void stop() {
        def debuggingRecordFile = new File("$project.rootDir$File.separator$Constant.DEBUG_PACKAGE$File.separator$Constant.DEBUG_MODULE_RECODE_FILE")
        def debuggingProjects = XmlUtils.parseDebuggingProjects(debuggingRecordFile)
        if (debuggingProjects.target) {
            clearProject(debuggingProjects.target)
            debuggingProjects.dependencies.each {
                clearProject(it)
            }
            removeGitModules()
            revertGitConfig()
            revertVersionsFile()
            if (debuggingRecordFile) {
                debuggingRecordFile.deleteOnExit()
            }
        }
    }

    /**
     * 删除.gitmodules 和./git/module文件夹下的内容
     */
    private void removeGitModules() {
        println "component:StopDebugTask:removeGitModules"
        def sOut = new StringBuilder()
        def sError = new StringBuffer()
        def process = "rm .gitmodules".execute(null, project.rootDir)
        process.consumeProcessOutput(sOut, sError)
        process.waitFor()

        process = "rm -rf .git/modules/*".execute(null, project.rootDir)
        process.consumeProcessOutput(sOut, sError)
        println sOut
        println sError
        process.waitFor()
    }

    /**
     * 恢复.git/config文件为初始状态
     */
    private void revertGitConfig() {
        def backupFile = new File("$project.rootDir$File.separator$Constant.DEBUG_PACKAGE$File.separator$Constant.GIT_CONFIG_BACKUP_FILE")
        if (backupFile.exists()) {
            def gitConfigFile = new File("$project.rootDir$File.separator" + ".git$File.separator" + "config")
            gitConfigFile.withPrintWriter {
                it.write(backupFile.text)
            }
        }
    }

    /**
     * 恢复versions.gradle 为初始状态
     */
    private void revertVersionsFile() {
        def backupFile = new File("$project.rootDir$File.separator$Constant.DEBUG_PACKAGE$File.separator$Constant.VERSIONS_BACKUP_FILE")
        if (backupFile.exists()) {
            def versionsFile = new File(project.rootDir, Constant.VERSIONS_FILE_NAME)
            versionsFile.withPrintWriter {
                it.write(backupFile.text)
                println "revertVersionsFile"
            }
        }
    }

    /**
     * 删除本地工作目录
     * @param project
     * @return
     */
    private void clearProject(Project target) {
        println "component:StopDebugTask:clearProject"
        def sOut = new StringBuilder()
        def sError = new StringBuffer()
        def process = "rm -rf $target.name".execute(null, project.rootDir)
        process.consumeProcessOutput(sOut, sError)
        process.waitFor()
        process = "git rm --cached $target.name".execute(null, project.rootDir)
        process.consumeProcessOutput(sOut, sError)
        process.waitFor()
    }

}