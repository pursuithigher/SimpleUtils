package com.dzbook.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import com.dzbook.lib.net.DzSchedulers;
import com.dzbook.lib.utils.ALog;
import com.dzbook.lib.utils.SDCardUtil;
import com.dzbook.lib.utils.StringUtil;
import com.dzpay.recharge.utils.PayLog;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.text.DecimalFormat;

/**
 * 文件的操作
 *
 * @author dllik
 * <p/>
 * 2013-11-23
 */
public class FileUtils extends AbsFileUtils {
    private static final int BUFFER_SIZE = 4 * 1024;
    private static final String LOG_TAG = "FileUtils: ";
    private static char mSeparatorChar = File.separatorChar;
    private static FileUtils ins;
    private String sdpath;


    private FileUtils() {
        sdpath = SDCardUtil.getInstance().getSDCardAndroidRootDir() + "/";
    }

    /**
     * 初始化
     */
    public void init() {
        sdpath = SDCardUtil.getInstance().getSDCardAndroidRootDir() + "/";
    }


    /**
     * 获取FileUtils实例
     *
     * @return 实例
     */
    public static FileUtils getDefault() {
        if (null == ins) {
            ins = new FileUtils();
        }
        return ins;
    }

    /**
     * 查看SD卡的剩余空间
     *
     * @return long
     */
    public static long getSDFreeSize() {
        //取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        //空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        //返回SD卡空闲大小
        //return freeBlocks * blockSize;  //单位Byte
        //return (freeBlocks * blockSize)/1024;   //单位KB
        return (freeBlocks * blockSize) / 1024 / 1024; //单位MB
    }


    /**
     * 在sd卡上创建文件夹（目录）
     *
     * @param dirName dirName
     * @return file
     */
    public File creatSDDir(String dirName) {
        File dir = new File(sdpath + dirName);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                ALog.dWz("mkdir success " + dirName);
            }
        } else {
            if (!dir.isDirectory()) {
                if (dir.delete()) {
                    ALog.dWz("delete file success " + dir);
                }
                if (dir.mkdirs()) {
                    ALog.dWz("mkdirs file success " + dirName);
                }
            }
        }
        return dir;
    }

    /**
     * 将指定内容写入指定目录的文件 如果指定目录文件存在，进行覆盖
     *
     * @param content content
     * @param path    path
     * @return boolean
     */

    public static boolean writeToLocalContent(String content, String path) {
        File file = new File(path);
        Writer writer = null;
        try {
            if (!file.getParentFile().exists()) {
                if (file.getParentFile().mkdirs()) {
                    ALog.dWz("mkdir success " + path);
                }
            }
            if (!file.exists()) {
                if (file.createNewFile()) {
                    ALog.dWz("createNewFile success " + path);
                }
            }
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), FileUtils.DEFAULT_CHARSET));
            writer.write(content);
            return true;
        } catch (IOException e) {
            ALog.printStackTrace(e);
            return false;
        } finally {
            closeStream(writer);
        }
    }

    /**
     * 从assets目录获取数据
     */
    static String getStringByAssetManager(AssetManager assetManager, String fileName) {
        InputStream inputStream = null;
        ByteArrayOutputStream outStream = null;
        try {
            inputStream = assetManager.open(fileName);
            outStream = new ByteArrayOutputStream(1024 * 50);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            return new String(outStream.toByteArray(), FileUtils.DEFAULT_CHARSET);
        } catch (IOException e) {
            ALog.printStackTrace(e);
        } finally {
            closeStream(outStream, inputStream);
        }
        return "";
    }

    /**
     * 删除文件夹中所有文件包括文件夹
     *
     * @param folderPath folderPath
     */
    public static void delFolder(String folderPath) {
        try {
            // 删除完里面所有内容
            delAllFile(folderPath);
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            if (myFilePath.delete()) {
                ALog.dWz("delFolder success " + folderPath);
            } // 删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除指定文件夹下所有文件
     *
     * @param path path
     * @return boolean
     */
    private static boolean delAllFile(String path) {
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            return false;
        }

        boolean flag = false;
        String[] tempList = file.list();
        if (tempList != null && tempList.length > 0) {
            File temp;
            for (String aTempList : tempList) {
                if (path.endsWith(File.separator)) {
                    temp = new File(path + aTempList);
                } else {
                    temp = new File(path + File.separator + aTempList);
                }
                if (temp.isFile()) {
                    boolean delete = temp.delete();
                    if (delete) {
                        ALog.dWz("delete file success");
                    }
                }
                if (temp.isDirectory()) {
                    // 先删除文件夹里面的文件
                    delAllFile(path + "/" + aTempList);
                    // 再删除空文件夹
                    delFolder(path + "/" + aTempList);
                    flag = true;
                }
            }
        }

        return flag;
    }

    /**
     * 当内存不足时 首先删掉用户相册缓存。
     * 再删系统崩溃日志
     * 再删掉用户的下载历史
     * 最后删除用户下载的系统更新包
     * 如果以上都清理不出空间，，再删自己的东西。
     *
     * @param delSize   传入要删的数值大小
     * @param pathArray 传递要删除的文件路径。
     * @return 返回实际上删除了多少空间
     */
    public static long delAllFileReturnSize(long delSize, String... pathArray) {
        long freeSize = delSize;
        if (freeSize <= 0) {
            freeSize = Long.MAX_VALUE;
        }
        long detSize = freeSize;
        if (null != pathArray) {
            for (String path : pathArray) {
                freeSize = delAllFileReturnSize(path, freeSize);
                if (freeSize <= 0) {
                    break;
                }
            }
        }
        return detSize - freeSize;
    }

    /**
     * 删除指定文件夹下的所有文件 并且返回删除的文件大小
     *
     * @param path    路径
     * @param delSize 递归时传递进去的数值跟初始调用的大小不一样
     * @return 剩余的需要删除的空间 long 型
     */
    private static long delAllFileReturnSize(String path, long delSize) {
        File file = new File(path);
        if (!file.exists()) {
            return delSize;
        }
        if (file.isFile()) {
            long fileLength = file.length();
            if (file.delete()) {
                delSize -= fileLength;
            }
            return delSize;
        } else {
            File[] tempList = file.listFiles();
            if (tempList != null && tempList.length > 0) {
                for (File temp : tempList) {
                    if (null == temp) {
                        continue;
                    }
                    delSize = delAllFileReturnSize(temp.getAbsolutePath(), delSize);
                    if (delSize <= 0) {
                        //当删够指定的大小了就不删除了
                        return delSize;
                    }
                }
            }
            if (file.delete()) {
                ALog.dWz("delete file success " + file);
            }
        }
        return delSize;
    }

    /**
     * 转换文件大小
     *
     * @param fileS fileS
     * @return 文件大小字符串
     */
    public static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";

        if (fileS > 0) {
            if (fileS < 1024) {
                fileSizeString = df.format((double) fileS) + " B";
            } else if (fileS < 1048576) {
                fileSizeString = df.format((double) fileS / 1024) + " K";
            } else if (fileS < 1073741824) {
                fileSizeString = df.format((double) fileS / 1048576) + " M";
            } else {
                fileSizeString = df.format((double) fileS / 1073741824) + " G";
            }
        } else {
            fileSizeString = "0.00 B";
        }

        return fileSizeString;
    }

    private static void writeRandomAccessFile(long kb, File file) {
        RandomAccessFile randomf = null;
        // 建立一个指定大小的空文件
        try {
            randomf = new RandomAccessFile(file, "rw");
            randomf.setLength(kb);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(randomf);
        }

    }

    /**
     * 创建指定大小的文件
     *
     * @param context context
     * @param path    path
     */
    public void createAssignSzieFile(Context context, final String path) {
        try {
            int time = SpUtil.getinstance(context).getInt("createAssignSzieFile_time");
            File file = new File(sdpath + path);
            if (!file.getParentFile().exists()) {
                if (file.getParentFile().mkdirs()) {
                    ALog.dWz("mkdirs success " + path);
                }
            }
            if (SDCardUtil.getInstance().isSDCardCanWrite(10 * 1024 * 1024)) {
                if (file.length() >= 0 && file.length() < 30 * 1024 * 1024 && time < 30) {
                    long length = file.length();
                    length += 1024 * 512;
                    // 建立一个指定大小的空文件  512kb  每一次添加512kb
                    writeRandomAccessFile(length, file);
                }
            }

        } catch (Exception e) {
            ALog.printStackTrace(e);
        }
    }

    /**
     * 设置文件尺寸
     * 如果文件小于10M则删除掉,否则大小减小5M
     *
     * @param file file
     */

    public static void setAssignSizeFile(File file) {

        RandomAccessFile randomf = null;

        try {
            if (file != null && file.exists()) {
                if (file.length() <= 10 * 1024 * 1024) {
                    if (file.delete()) {
                        ALog.dWz("delete file success " + file);
                    }
                } else {
                    // 设置文件尺寸
                    long length = file.length() - 5 * 1024 * 1024;
                    randomf = new RandomAccessFile(file, "rw");
                    randomf.setLength(length);
                }
            }
        } catch (Exception e) {
            ALog.printStackTrace(e);
        } finally {
            closeStream(randomf);
        }

    }


    /**
     * 判断路径是否存在
     *
     * @param path 路径
     * @return 如果条件成立，返回true
     */
    public static boolean exists(String path) {
        return !TextUtils.isEmpty(path) && new File(path).exists();
    }

    /**
     * 判断路径是文件，且存在
     *
     * @param path 文件路径，如果传入null字符串，则认为文件不存在
     * @return 如果条件成立，返回true;
     */
    public static boolean fileExists(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        return file.exists() && file.isFile();
    }

    /**
     * 创建文件， 如果不存在则创建，否则返回原文件的File对象
     *
     * @param path 文件路径
     * @return 创建好的文件对象, 返回为空表示失败
     */
    private static synchronized File createFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        File file = new File(path);
        if (file.isFile()) {
            return file;
        }

        File parentFile = file.getParentFile();
        if (parentFile != null && (parentFile.isDirectory() || parentFile.mkdirs())) {
            try {
                if (file.createNewFile()) {
                    return file;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * 删除文件或目录
     *
     * @param path 文件或目录路径。
     * @return true 表示删除成功，否则为失败
     */
    public static synchronized boolean delete(String path) {
        return !TextUtils.isEmpty(path) && delete(new File(path));
    }

    /**
     * 删除文件或目录
     *
     * @param path 文件或目录。
     * @return true 表示删除成功，否则为失败
     */
    public static synchronized boolean delete(File path) {
        if (path.isDirectory()) {
            File[] files = path.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (!delete(file)) {
                        return false;
                    }
                }
            }
        }
        return !path.exists() || path.delete();
    }

    /**
     * 读取文件内容,并以字符串形式返回
     *
     * @param path 文件路径
     * @return 文件内容
     */
    public static String load(String path) {
        if (path == null) {
            throw new NullPointerException("path should not be null.");
        }

        String string = null;
        try {
            string = StringUtil.stringFromInputStream(new FileInputStream(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return string != null ? string : "";
    }

    /**
     * 将字符串数据保存到文件.
     * <br/>注意：如果没有目录则会创建目录
     *
     * @param content 字符串内容
     * @param path    文件路径
     * @return 成功返回true, 否则返回false
     */
    public static boolean store(String content, String path) {
        if (path == null) {
            throw new NullPointerException("path should not be null.");
        }


        BufferedWriter bufferedWriter = null;
        try {
            File file = createFile(path);
            if (file == null) {
                ALog.dZz(LOG_TAG + "file == null path=%s", path);
                //可能无存储卡或者其他原因导致
                return false;
            }
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), FileUtils.DEFAULT_CHARSET));
            bufferedWriter.write(content != null ? content : "");
            bufferedWriter.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(bufferedWriter);
        }
        return false;
    }

    /**
     * 将输入流保存到文件，并关闭流.
     *
     * @param inputStream 字符串内容
     * @param path        文件路径
     * @return boolean
     */
    public static synchronized boolean store(InputStream inputStream, String path) {
        if (path == null) {
            throw new NullPointerException("path should not be null.");
        }
        int length;

        FileOutputStream fileOutputStream = null;

        try {
            File file = createFile(path);
            if (file == null) {
                ALog.dZz(LOG_TAG + "inputStream file == null path=%s", path);
                //可能无存储卡或者其他原因导致
                return false;
            }
            byte[] buffer = new byte[BUFFER_SIZE];
            fileOutputStream = new FileOutputStream(file);
            while ((length = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, length);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != fileOutputStream) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    ALog.printStackTrace(e);
                }
            }
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    ALog.printStackTrace(e);
                }
            }
        }
        return false;
    }

    /**
     * 复制文件
     *
     * @param desPath 目标文件路径
     * @param srcPath 源文件路径
     * @return false if file copy failed, true if file copy succeeded..
     */
    public static boolean copy(String desPath, String srcPath) {
        if (desPath == null || srcPath == null) {
            throw new NullPointerException("path should not be null.");
        }
        FileInputStream input = null;
        boolean succeed;

        try {
            input = new FileInputStream(srcPath);
            succeed = FileUtils.store(input, desPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeStream(input);
        }

        return succeed;
    }

    /**
     * 获取本地文件或URL的文件名. 包含后缀
     *
     * @param path 本地文件或URL路径
     * @return 文件名
     */
    public static String getFileName(String path) {
        if (TextUtils.isEmpty(path)) {
            return "";
        }

        int query = path.lastIndexOf('?');
        if (query > 0) {
            path = path.substring(0, query);
        }

        int filenamePos = path.lastIndexOf(mSeparatorChar);
        return (filenamePos >= 0) ? path.substring(filenamePos + 1) : path;
    }

    /**
     * 按参数顺序，关闭流。
     *
     * @param closeableList closeableList
     */
    public static void closeStream(Closeable... closeableList) {
        if (null != closeableList) {
            for (Closeable closeable : closeableList) {
                if (null != closeable) {
                    try {
                        closeable.close();
                    } catch (IOException e) {
                        ALog.printStackTrace(e);
                    }
                }
            }
        }
    }

    /**
     * 判断.ishugui文件是否是个文件，
     * 如果是文件则删除后重新建立
     * AppContext.APP_ROOT_DIR_PATH
     */
    public static void handleAppFileRootDirectory() {
        String path = SDCardUtil.getInstance().getSDCardAndroidRootDir() + File.separator + APP_ROOT_DIR_PATH;
        File file = new File(path);
        if (file.exists()) {
            if (!file.isDirectory()) {
                if (file.delete()) {
                    File ishuguiFile = new File(path);
                    if (!ishuguiFile.exists()) {
                        FileUtils.getDefault().creatSDDir(APP_ROOT_DIR_PATH);
                    }
                }
            }
        } else {
            FileUtils.getDefault().creatSDDir(APP_ROOT_DIR_PATH);
        }
    }

    /**
     * 为了上线版本不打应日志并调试方便
     */
    public static void logSwitch() {
        DzSchedulers.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (PackageControlUtils.isDriectOpenAlog()) {
                        ALog.setDebugMode(true);
                        return;
                    }
                    boolean existLogFlag = false;
                    if (SDCardUtil.getInstance().isSDCardAvailable()) {
                        String path = Environment.getExternalStorageDirectory() + File.separator + "mdz";
                        File file = new File(path);
                        existLogFlag = file.exists();
                    }

                    if (existLogFlag) {
                        ALog.setDebugMode(true);
                        PayLog.setDebugMode(true);
                    } else {
                        ALog.setDebugMode(false);
                        PayLog.setDebugMode(false);
                    }
                } catch (Exception e) {
                    ALog.printExceptionWz(e);
                }
            }
        });

    }

    /**
     * Returns an array of abstract pathnames denoting the files in the
     * directory denoted by this abstract pathname.
     *
     * @param file file
     * @return An array of abstract pathnames denoting the files and
     * directories in the directory denoted by this abstract pathname.
     */
    public static File[] getListFile(File file) {
        if (null == file || !file.exists() || !file.isDirectory()) {
            return null;
        }
        return file.listFiles();
    }


}
