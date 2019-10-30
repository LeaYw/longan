package com.foryou.component

import com.foryou.component.bean.DebuggingProjects
import com.foryou.component.bean.Project
import com.foryou.component.bean.Projects

/**
 * Description:
 * Created by liyawei
 * Date:2019-08-13 17:07
 * Email:liyawei@foryou56.com
 */

public class XmlUtils {

    static DebuggingProjects parseDebuggingProjects(File file) {
        println "component:XmlUtils:parseDebuggingProjects"
        DebuggingProjects debuggingProjects = new DebuggingProjects()

        if (file.exists()) {
            def node = new XmlParser().parse(file)
            debuggingProjects.target = getProjectByNode(node.target[0])
            node.project.each { projectNode ->
                Project project = getProjectByNode(projectNode)
                debuggingProjects.dependencies.add(project)
            }
        }
        debuggingProjects
    }

    private static Project getProjectByNode(Node projectNode) {
        println "component:XmlUtils:getProjectByNode:project="
        def project = new Project()
        project.name = projectNode.attribute("name")
        project.artifactId = projectNode.artifactId[0].text()
        project.groupId = projectNode.groupId[0].text()
        project.build_project = projectNode.build_project[0].text()
        project.url = projectNode.url[0].text()
        println project
        project
    }

    static Projects parse(String path) {
        println "component:XmlUtils:parse:path=$path"
        def parser = new XmlParser()
        parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
        parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
        def node = parser.parse(path)
        Projects projects = new Projects()
        node.project.each { projectNode ->
            Project project = getProjectByNode(projectNode)
            projects.list.add(project)
        }
        projects
    }

}