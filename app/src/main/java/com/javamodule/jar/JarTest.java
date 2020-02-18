package com.javamodule.jar;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class JarTest {

    public static void main(String[] args) {
        //app\src\main\assets\rxandroid-0.23.0.jar
        System.out.println(System.getProperty("user.dir"));
        String filePath = System.getProperty("user.dir") + File.separator + "app" + File.separator + "src" + File.separator + "main" + File.separator + "assets";
        File mfile = new File(filePath,"rxandroid-0.23.0.jar");

        try {
            JarFile jarFile = new JarFile(mfile);
            Manifest manifest = jarFile.getManifest();
            Attributes entries1 = manifest.getMainAttributes();
            Set<Map.Entry<Object, Object>> entrySet = entries1.entrySet();
            // 遍历Jar里面的Manifest文件内容
            for(Map.Entry<Object, Object> objectEntry : entrySet) {
                System.out.println(objectEntry.getKey() + ":"+ objectEntry.getValue());
            }
            JarEntry entry;
            // 遍历Jar文件里面的所有资源名，nextElement会递归遍历
            for (Enumeration<JarEntry> entries = jarFile.entries();entries.hasMoreElements(); ) {
                entry = entries.nextElement();
                System.out.println(entry.getName());
                if(entry.getName().contains("rxandroid.properties")){
                    writeFile(jarFile.getInputStream(entry),new File(filePath,"aa.properties"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(InputStream is, File file) throws Exception {

        if (file != null) {

            //推荐使用字节流读取，因为虽然读取的是文件，如果是 .exe, .c 这种文件，用字符流读取会有乱码
            OutputStream os = new BufferedOutputStream(new FileOutputStream(file));

            byte[] bytes = new byte[2048]; //这里用小数组读取，使用file.length()来一次性读取可能会出错（亲身试验）

            int len;

            while ((len = is.read(bytes)) != -1) {
                os.write(bytes, 0, len);
            }

            os.close();
        }
    }
}
