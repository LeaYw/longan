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
            debuggingProjects.dependencies.forEach({Project pro->
                def sOut = new StringBuilder()
                def sError = new StringBuffer()
                def process = "git rm --cached $pro.name".execute(null, project.rootDir)
                process.consumeProcessOutput(sOut, sError)
                process.waitFor()

                def projectFile = new File(project.rootDir, pro.name)
                projectFile.deleteDir()
            })

            removeGitModules()
            revertGitConfig()
            revertVersionsFile()
            deleteDebugPackage()
        }
    }

    /**
     * 删除.gitmodules 和./git/module文件夹下的内容
     */
    private void removeGitModules() {
        def gitmodules = new File(project.rootDir, ".gitmodules")
        gitmodules.delete()
        def modulesDir = new File(project.rootDir, ".git/modules")
        modulesDir.deleteDir()
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
            }
        }
    }

    /**
     * 删除本地工作目录
     * @param project
     * @return
     */
    private void clearProject(Project target) {
        def sOut = new StringBuilder()
        def sError = new StringBuffer()
        def process = "git rm --cached $target.name".execute(null, project.rootDir)
        process.consumeProcessOutput(sOut, sError)
        process.waitFor()

        def projectFile = new File(project.rootDir, target.name)
        projectFile.deleteDir()
    }

    private void deleteDebugPackage(){
        def debugPackage = new File(project.rootDir,Constant.DEBUG_PACKAGE)
        debugPackage.deleteDir()
    }

}