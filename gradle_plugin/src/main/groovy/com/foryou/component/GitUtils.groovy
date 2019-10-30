package com.foryou.component

import com.foryou.component.bean.Project


/**
 * Description:
 * Created by liyawei
 * Date:2019-08-13 17:07
 * Email:liyawei@foryou56.com
 */

public class GitUtils {

    static void addSubmodule(File rootDir, Project project) {
        addSubmodule(rootDir, project, null)
    }

    static void addSubmodule(File rootDir, Project project, String branch) {
        println "component:GitUtils:addSubmodule:project=$project"
        if (branch == null || branch.empty) {
            branch = Constant.DEFAULT_BRANCH
        }
        def projectFile = new File(project.name)
        projectFile.deleteOnExit()
        def sOut = new StringBuilder(), sError = new StringBuilder()
        def cmd = "git submodule add -b $branch $project.url "
        println cmd
        def proc = cmd.execute(null, rootDir)
        proc.consumeProcessOutput(sOut, sError)
        proc.waitFor()
        println sOut
        println sError
    }

    static void cloneProject(String rootDir, String path) {
        println "component:GitUtils:cloneProject:rootDir=$rootDir path=$path"
        def debugPackage = new File("$rootDir$File.separator$Constant.DEBUG_PACKAGE")
        def file = new File(debugPackage, Constant.PROJECT_MP)
        file.deleteDir()
        def cmd = "git clone $path "
        def proc = cmd.execute(null, debugPackage)
        proc.waitFor()
    }
}