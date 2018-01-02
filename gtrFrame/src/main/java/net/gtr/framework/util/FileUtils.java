/*
 * Copyright (c) 2017. heisenberg.gong
 */

package net.gtr.framework.util;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * 文件处理的工具类
 *
 * @author heisenberg.gong
 */
public class FileUtils {

    /**
     * Deletes all files and subdirectories under "dir".
     *
     * @param dir Directory to be deleted
     * @return boolean Returns "true" if all deletions were successful.
     * <p>
     * If a deletion fails, the method stops attempting to
     * <p>
     * delete and returns "false".
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] list = dir.list();
            if (list.length == 0) {
                return dir.delete();
            }
            for (String children : list) {
                boolean success = deleteDir(new File(dir, children));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so now it can be smoked
        return dir.delete();

    }

    /**
     * 获取文件或文件夹的大小
     *
     * @param file 文件或文件夹
     * @return 单位byte
     */
    public static long getFileSize(File file) {
        return calculatorSize(file, 0);
    }

    /**
     *
     * @param file 文件
     * @param fileSize 文件初始长度
     * @return 返回累计的文件长度
     */
    private static long calculatorSize(File file, long fileSize) {
        // 判断是不是文件
        if (file.isFile()) {
            fileSize += file.length();
        } else {
            // 文件夹的方式,获取文件夹下的子文件
            File[] files = file.listFiles();
            if (files == null) {
                // 如果没有子文件, 文件夹大小为0
                fileSize += 0;
            } else {
                for (File childFile : files) {
                    fileSize = calculatorSize(childFile, fileSize);
                }
            }
        }
        return fileSize;
    }

    /**
     * 判断SDCard是否存在 [当没有外挂SD卡时，内置ROM也被识别为存在sd卡]
     *
     * @return
     */
    private static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡根目录路径
     *
     * @return
     */
    private static String getSdCardPath() throws IOException {
        boolean exist = isSdCardExist();
        String path;
        if (exist) {
            path = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
        } else {
            throw new IOException("SD卡路径无法获取");
        }
        return path;

    }

    /**
     * 获取Log根目录路径：系统提供的目录
     *
     * @return 系统提供的目录
     */
    public static String getLogSysPath() throws IOException {
        boolean exist = isSdCardExist();
        if (exist) {
            String path = getSdCardPath() + "/bugreport/yingyong/";
            File logFile = new File(path);
            if (!logFile.exists()) {
                throw new IOException("系统日志文件夹无法获取");
            }
            return path;
        } else {
            throw new IOException("SD卡路径无法获取");
        }

    }

    /**
     * 获取Log根目录路径:自己建立的目录
     *
     * @return 自己建立的目录
     */
    static String getLogAppPath() throws IOException {
        boolean exist = isSdCardExist();
        if (exist) {
            return getSdCardPath()  + "/AAA_HSLogFile/";
//            return BaseApp.getContext().getFilesDir().getParentFile() + "/LogFile/HS";
        } else {
            throw new IOException("SD卡路径无法获取");
        }
    }
}
