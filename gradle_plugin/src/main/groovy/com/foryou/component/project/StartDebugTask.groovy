package com.foryou.component.project


import com.foryou.component.Constant
import com.foryou.component.DependencyUtils
import com.foryou.component.XmlUtils
import com.foryou.component.bean.DebuggingProjects
import com.foryou.component.bean.Project
import com.foryou.component.bean.Projects
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import com.foryou.component.FileUtils
import com.foryou.component.GitUtils

class StartDebugTask extends DefaultTask {

    @TaskAction
    void start() {
        String targetModule = project["Module"]//eg:com.foryou.common:corelib:1.0.0
        String branch = project["Branch"]//eg:dev

        println "component:targetModule=$targetModule"
        println "component:branch=$branch"
        backupGitConfig()
        def debuggingProjects = analyzeDependencies(targetModule)
        if (debuggingProjects.target) {
            GitUtils.addSubmodule(project.rootDir, debuggingProjects.target, branch)
            debuggingProjects.dependencies.each {
                GitUtils.addSubmodule(project.rootDir,it)
            }

            File debuggingRecordFile = initRecordFile()
            FileUtils.refreshDebuggingProjects(debuggingRecordFile, debuggingProjects)

            def versionsFile = new File(Constant.VERSIONS_FILE_NAME)
            backupVersionsFile(versionsFile)
            FileUtils.changeVersions(versionsFile, debuggingProjects)
        }
    }

    private File initRecordFile() {
        println "component:initRecordFile"
        def debugPackage = new File(project.rootDir, Constant.DEBUG_PACKAGE)
        if (!debugPackage.exists()) {
            debugPackage.mkdir()
        }
        def debuggingRecordFile = new File(debugPackage, Constant.DEBUG_MODULE_RECODE_FILE)
        if (!debuggingRecordFile.exists()){
            debuggingRecordFile.createNewFile()
        }
        debuggingRecordFile
    }

    /**
     * 分析依赖获取DebuggingProjects
     * @param targetModule 要debug的组件名称
     * @return DebuggingProjects
     */
    private DebuggingProjects analyzeDependencies(String targetModule) {
        println "component:analyzeDependencies:targetModule=$targetModule"
        def dependencies = DependencyUtils.analyzeDependency(project, targetModule)
        GitUtils.cloneProject(project.rootDir.path, Constant.PROJECTS_ADDRESS)
        Projects projects = XmlUtils.parse("$project.rootDir$File.separator$Constant.DEBUG_PACKAGE$File.separator$Constant.PROJECT_MP$File.separator$Constant.PROJECT_MP_FILE")
        def debuggingProjects = new DebuggingProjects()
        dependencies.each { dependent ->
            def findProject = projects.list.find { Project project ->
                dependent.contains(project.groupId + project.artifactId)
            }
            if (findProject) {
                println findProject
                debuggingProjects.dependencies.add(findProject)
            }
        }

        def targetProject = projects.list.find { Project project ->
            targetModule.contains("$project.groupId:$project.artifactId")
        }
        println "target:$targetProject"
        debuggingProjects.target = targetProject

        return debuggingProjects
    }

    /**
     * 备份.git/config 文件
     */
    private void backupGitConfig() {
        println "component:backupGitConfig"
        def file = new File("$project.rootDir$File.separator" + ".git$File.separator" + "config")
        def debugPackage = new File("$project.rootDir$File.separator$Constant.DEBUG_PACKAGE")
        if (!debugPackage.exists()) {
            debugPackage.mkdir()
        }
        def backupFile = new File(debugPackage, Constant.GIT_CONFIG_BACKUP_FILE)
        if (!backupFile.exists()) {
            backupFile.createNewFile()
        }
        backupFile.withPrintWriter {
            it.write(file.text)
        }
    }

    /**
     * 备份fy_versions.gradle
     * @param versionsFile
     */
    private void backupVersionsFile(File versionsFile) {
        println "component:backupVersionsFile"
        def debugPackage = new File("$project.rootDir$File.separator$Constant.DEBUG_PACKAGE")
        if (!debugPackage.exists()) {
            debugPackage.mkdir()
        }
        def backupFile = new File(debugPackage, Constant.VERSIONS_BACKUP_FILE)
        if (!backupFile.exists()) {
            backupFile.createNewFile()
        }
        backupFile.withPrintWriter {
            it.println(versionsFile.text)
        }
    }

}