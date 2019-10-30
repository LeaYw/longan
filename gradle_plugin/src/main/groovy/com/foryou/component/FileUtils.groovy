package com.foryou.component


import com.foryou.component.bean.DebuggingProjects
import com.foryou.component.bean.Project
import groovy.xml.MarkupBuilder

/**
 * Description:
 * Created by liyawei
 * Date:2019-08-13 17:07
 * Email:liyawei@foryou56.com
 */

public class FileUtils {

    /**
     * 向记录文件
     * @param file
     * @param target
     */
    static void refreshDebuggingProjects(File file, DebuggingProjects debuggingProjects) {
        println "component:FileUtils:refreshDebuggingProjects"
//        def node = new Node(null, Constant.NODE_PROJECTS_NAME)
//        node.append(generateProjectNode(debuggingProjects.target))
//        debuggingProjects.dependencies.each { project ->
//            Node projectNode = generateProjectNode(project)
//            node.appendNode(projectNode)
//        }
//        file.withPrintWriter {
//            println node.text()
//            it.write(node.text())
//        }

        def mb = new MarkupBuilder(file.newPrintWriter())
        mb.projects() {
            mb.target(name: debuggingProjects.target.name) {
                groupId(debuggingProjects.target.groupId)
                artifactId(debuggingProjects.target.artifactId)
                url(debuggingProjects.target.url)
                build_project(debuggingProjects.target.build_project)
            }
            debuggingProjects.dependencies.each { Project project ->
                mb.project(name: project.name) {
                    groupId(project.groupId)
                    artifactId(project.artifactId)
                    url(project.url)
                    build_project(project.build_project)
                }
            }
        }
    }

    static void changeVersions(File versionsFile, DebuggingProjects debuggingProjects) {
        println "component:FileUtils:changeVersions"
        if (!versionsFile.exists()) {
            println "没有找到$Constant.VERSIONS_FILE_NAME"
            return
        }
        if (!debuggingProjects.target) {
            println "没有找到需要debug的组件"
            return
        }
        def map = [:]
        def target = debuggingProjects.target
        map."$target.groupId:$target.artifactId" = target

        debuggingProjects.dependencies.each {
            map."$it.groupId:$it.artifactId" = it
        }
        def text = versionsFile.text
        def replaceText = text
        versionsFile.eachLine { line ->
            def find = map.find { entry ->
                line.contains(entry.key)
            }
            if (find != null) {
                Project targetProject = find.value
                def split = line.split("=")
                if (split.length == 2) {
                    println split[1]
                    replaceText = replaceText.replace(split[1], " project (':$targetProject.name:$targetProject.artifactId')")
                    println "modify dependence $line"
                }
            }
        }
        versionsFile.withPrintWriter { writer ->
            println replaceText
            writer.println(replaceText)
            println "$versionsFile.name 替换完成"
        }
    }
}