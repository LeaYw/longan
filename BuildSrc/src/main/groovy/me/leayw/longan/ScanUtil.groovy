package me.leayw.longan

import com.android.SdkConstants
import javassist.ClassPool
import javassist.CtClass

import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * Scan all class implemented me.leayw.longan_api.AppProxy
 * @author billy.qi email: qiyilike@163.com
 * @since 17/3/20 11:48
 */
class ScanUtil {

    /**
     * scan jar file
     * @param jarFile All jar files that are compiled into apk
     * @param destFile dest file after this transform
     */
    static void scanJar(CtClassContainer container, ClassPool classPool, File jarFile, File destFile) {
        if (jarFile) {
            classPool.insertClassPath(jarFile.absolutePath)
            def file = new JarFile(jarFile)
            Enumeration enumeration = file.entries()
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                String entryName = jarEntry.getName()
                if (entryName.endsWith(SdkConstants.DOT_CLASS)) {
                    String className = entryName.substring(0, entryName.length() - SdkConstants.DOT_CLASS.length()).replaceAll('/', '.')
                    if (CtClassContainer.NAME_INJECT_TO_CLASS == className) {
                        // mark this jar file contains LogisticsCenter.class
                        // After the scan is complete, we will generate register code into this file
                        container.destPath = destFile.absolutePath
                        container.logisticsCenter = classPool.get(className)
                    } else if (shouldProcessClass(className)) {
                        container.classNames.add(className)
                    }
                }
            }
            file.close()
        }
    }

    static void initAppProxy(CtClassContainer container, ClassPool pool) {
        container.classNames.each {
            try {
                CtClass ctClass = pool.get(it)
                if (!ctClass.isInterface()) {
                    boolean add = false
                    ctClass.interfaces.each {
                        if (it.name == CtClassContainer.NAME_APP_PROXY) {
                            add = true
                        }
                    }
                    if (add) {
                        container.appProxies.add(ctClass)
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    static boolean shouldProcessClass(String name) {
        return !name.startsWith("android")
    }

    static boolean shouldProcessClass(CtClass ctClass) {
        def add = false
        if (!ctClass.interface) {
            println ctClass.interface.toString() + "=============================================="
            ctClass.interfaces.each {
                if (it.name == CtClassContainer.NAME_APP_PROXY) {
                    add = true
                }
            }
        }
        return add

    }

}