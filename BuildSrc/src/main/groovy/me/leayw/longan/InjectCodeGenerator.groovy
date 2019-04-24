package me.leayw.longan

import com.android.SdkConstants
import javassist.CtClass
import org.apache.commons.io.FileUtils

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * generate register code into LogisticsCenter.class
 */
class InjectCodeGenerator {

    static void generate(CtClassContainer container) {
        container.logisticsCenter.defrost()
        def method = container.logisticsCenter.getDeclaredMethod(CtClassContainer.NAME_METHOD_INJECT)
        method.insertAfter(getBody(container.appProxies))
        def bytecode = container.logisticsCenter.toBytecode()
        container.logisticsCenter.detach()
        writeToJar(container.destPath, bytecode, CtClassContainer.NAME_INJECT_TO_CLASS + SdkConstants.DOT_CLASS)
    }

    private static String getBody(List<CtClass> appProxies) {
        StringBuilder builder = new StringBuilder()
        appProxies.each {
            println "Longan: $it.name is injecting"
            builder.append("$CtClassContainer.NAME_METHOD_REGISTER(new $it.name());")
        }
        return builder.toString()
    }

    static void writeToJar(String jarPath, byte[] bytes, String fileName) {
        File jarFile = new File(jarPath)
        File tempJarFile = new File(jarFile.parent, jarFile.name + ".tmp")
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
            def zipEntry = new ZipEntry(entry.name)
            tempJar.putNextEntry(zipEntry)
            def entryName = entry.name.replace("/", ".")
            if (entryName != fileName) {
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
        FileUtils.copyFile(tempJarFile, jarFile)
        if (tempJarFile.exists()) {
            tempJarFile.delete()
        }
    }
}