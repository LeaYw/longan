package com.foryou.library

import com.android.SdkConstants
import com.android.build.api.transform.TransformInput
import javassist.ClassPool
import javassist.CtClass
import javassist.NotFoundException
import org.apache.commons.io.FileUtils

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.regex.Matcher
import java.util.zip.ZipEntry

class ConvertUtils {
    static List<CtClass> toCtClasses(Collection<TransformInput> inputs, ClassPool classPool) {
        List<String> classNames = new ArrayList<>()
        List<CtClass> allClass = new ArrayList<>()
        inputs.each {
            it.directoryInputs.each {
                def dirPath = it.file.absolutePath
                if (shouldProcessClass(dirPath)) {
                    classPool.insertClassPath(it.file.absolutePath)
                    FileUtils.listFiles(it.file, null, true).each {
                        if (it.absolutePath.endsWith(SdkConstants.DOT_CLASS)) {
                            def className = it.absolutePath.substring(dirPath.length() + 1, it.absolutePath.length() - SdkConstants.DOT_CLASS.length()).replaceAll(Matcher.quoteReplacement(File.separator), '.')
                            if (classNames.contains(className)) {
                                throw new RuntimeException("You have duplicate classes with the same name : " + className + " please remove duplicate classes ")
                            }
                            classNames.add(className)
                        }
                    }
                }
            }

            it.jarInputs.each {
                classPool.insertClassPath(it.file.absolutePath)
                def jarFile = new JarFile(it.file)
                Enumeration<JarEntry> classes = jarFile.entries()
                while (classes.hasMoreElements()) {
                    JarEntry libClass = classes.nextElement()
                    String className = libClass.getName()
                    if (className.endsWith(SdkConstants.DOT_CLASS)) {
                        className = className.substring(0, className.length() - SdkConstants.DOT_CLASS.length()).replaceAll('/', '.')
                        if (classNames.contains(className)) {
                            throw new RuntimeException("You have duplicate classes with the same name : " + className + " please remove duplicate classes ")
                        }
                        if (className == "com.foryou.longan_api.core.LogisticsCenter"){
                            println "^^^^^^^^^^^^^^^^^^^^^^^^^^" + it.file.absolutePath
                        }
                        classNames.add(className)
                    }
                }
                jarFile.close()
            }
        }
        classNames.each {
            try {
                allClass.add(classPool.get(it))
            } catch (NotFoundException e) {
                println "class not found exception class name:  $it ,$e.getMessage()"
            }
        }
        return allClass
    }

    static boolean shouldProcessClass(String path) {
        true
    }

    static void writeToJar(String jarPath, byte[] bytes, String fileName) {
        println "start write to jar"
        File jarFile = new File(jarPath)
        File tempJarFile = new File(jarFile.parent,jarFile.name + ".tmp")
        if (tempJarFile.exists()) {
            tempJarFile.delete()
        }

        JarFile jar = new JarFile(jarFile)
        JarOutputStream tempJar = new JarOutputStream(new FileOutputStream(tempJarFile))
        byte[] buffer = new byte[1024]
        int bytesRead


        Enumeration entries = jar.entries()
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement()
            InputStream entryStream = jar.getInputStream(entry)

            def entryName = entry.name
            def zipEntry = new ZipEntry(entryName)

            tempJar.putNextEntry(zipEntry)
            println "//////////////////////" + entry.name
            if (entry.name != fileName) {
                while ((bytesRead = entryStream.read(buffer)) != -1) {
                    tempJar.write(buffer, 0, bytesRead)
                }
            } else {
                tempJar.write(bytes)
            }
            entryStream.close()
            tempJar.closeEntry()
        }
        tempJar.close()
        jar.close()
        if (jarFile.exists()) {
            jarFile.delete()
        }
        FileUtils.copyFile(tempJarFile,jarFile)
        if (tempJarFile.exists()){
            tempJarFile.delete()
        }
//        tempJarFile.renameTo(jarFile)
        println jarPath + " write success."
    }

}