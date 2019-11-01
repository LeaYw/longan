package com.foryou.component

import com.foryou.component.bean.TreeNode
import org.gradle.api.Project

import java.util.regex.Pattern

public class DependencyUtils {

    public static List<String> analyzeDependency(Project project, String targetModule) {
        def sOut = new StringBuilder(), sError = new StringBuilder()
        def process = "./gradlew  -q :$project.name:dependencies --configuration debugRuntimeClasspath".execute()
        process.consumeProcessOutput(sOut, sError)
        process.waitFor()
        def file = new File("$project.rootDir$File.separator$Constant.DEBUG_PACKAGE$File.separator$Constant.DEPENDENCY_TEMP_FILE")
        if (sOut.contains("debugRuntimeClasspath")) {
            file.withPrintWriter { printWriter ->
                printWriter.println(sOut)
            }
            def treeNodes = makeTree(file, targetModule)
            def list = new ArrayList<String>()
            treeNodes.each {
                list.add(it.data)
            }
            list
        } else {
            println "依赖树读取失败"
            Collections.emptyList()
        }
    }

    private static ArrayList<TreeNode> makeTree(File file, String upgradeProject) {
        def regex1 = ~/^([|+\\\s]).*(foryou|project)+.*/
        def regex = ~/ {4}/
        def regex2 = ~/((\w)+\.(\w)+\.(\w)+:([(\w|\d)+ ])+)|(project\s+:([^#\d  :])+)/
        def treeNode = new TreeNode("RootProject", 0, 0)
        Map<Integer, TreeNode> map = new HashMap()
        def function = { String text ->
            def compile = Pattern.compile(regex2 as String)
            def matcher = compile.matcher(text)
            if (matcher.find()) {
                return matcher.group(0)
            }
        }
        map.put(0, treeNode)
        def index = 1
        file.eachLine {
            if (it.matches(regex1)) {
                int count = 1
                def compile = Pattern.compile(regex as String)
                def matcher = compile.matcher(it)
                while (matcher.find()) {
                    count++
                }
                def get = map.get(count - 1)
                if (get != null) {
                    def childrenTreeNode = new TreeNode(function.call(it), index, count)
                    get.addChild(childrenTreeNode)
                    map.put(count, childrenTreeNode)
                }
                index++
            }
        }

        println("已经得到依赖关系")
        treeNode.traverse(1)


        def list = treeNode.findTreeNodeByName(upgradeProject)
        def parentList = new ArrayList<TreeNode>()

        //打找出所要升级的全部父类
        list.forEach { it ->
            parentList.addAll(it.getElders())
        }

        parentList = parentList.unique()

        println("全部Module 线性关系如下：")

        println parentList.toListString()

        parentList
    }
}