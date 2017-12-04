package com.millet.androidlib.Utils;

import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

/**
 * SD卡工具类
 * Created by Administrator on 2017/1/20.
 */

public class SDCardUtils {

    //外部存储：Environment.getExternalStorageDirectory() /storage/emulated/0删除应用不会删除该目录下文件，一般会手动添加Application Package目录。卸载不会删除
    //推荐使用//外部存储：_context.getExternalFilesDir(dir)，路径为:/mnt/sdcard/Android/data/< package name >/files/…。卸载会删除
    //外部缓存目录：_context.getExternalCacheDir()，路径为:/mnt/sdcard//Android/data/< package name >/cache/…。卸载和清理缓存会删除
    //内部存储：_context.getFilesDir()，路径是:/data/data/< package name >/files/…。卸载会删除
    //内部缓存存储：_context.getCacheDir()，	路径是:/data/data/< package name >/cache/…。卸载和清理缓存会删除


    //二级目录
    private static String BASE_DIR = "millet";

    public static final String PHOTO_SUFFIX = ".jpgt";

    /**
     * 初始化SD卡存储路径
     *
     * @param _dir 传入的根路径名
     */
    public static File initSDDir(String _dir) {
        if (!isSDCardEnable()) return null;
        if (!BASE_DIR.equals(_dir)) BASE_DIR = _dir;
        File _file = new File(getSDCardFile(), _dir);
        if (!_file.exists()) {
            _file.mkdirs();
        }
        return _file;
    }

    /**
     * 判断SDCard是否可用
     *
     * @return
     */
    public static boolean isSDCardEnable() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable()) {
            return true;
        }
        return false;
    }

    /**
     * 获取SD卡路径
     *
     * @return
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 获取SD卡文件目录，一级目录下，获取到的/storage/emulated/0，区别于context.getExternalCacheDir()的路径，/storage/emulated/0/Android/data/<application package></>/cache
     *
     * @return
     */
    public static File getSDCardFile() {
        return Environment.getExternalStorageDirectory();
    }

    /**
     * 判断一级目录下面文件是否存在，在程序中
     *
     * @param _name
     * @return
     */
    public static boolean existFileByName(String _name) {
        if (TextUtils.isEmpty(_name)) return false;
        File _file = new File(getSDCardFile(), appendBase(_name));
        if (_file.exists())
            return true;
        else
            return false;
    }

    /**
     * 判断该路径是否存在，分为程序中和系统中
     *
     * @param _pathName
     * @return
     */
    public static boolean existFileByPath(String _pathName) {
        if (TextUtils.isEmpty(_pathName)) return false;
        File _file = new File(_pathName);
        if (_file.exists())
            return true;
        else
            return false;
    }

    /**
     * 创建文件，在一级目录下创建文件
     *
     * @param _name
     * @return
     */
    public static File createFileByName(String _name) {
        if (TextUtils.isEmpty(_name)) return null;
        File _file = new File(getSDCardFile(), appendBase(_name));
        if (!_file.exists()) {
            try {
                _file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return _file;
    }

    /**
     * 创建二级子目录，在一级目录下创建，也可以传入millet/millet/...格式
     *
     * @param _subDirName
     */
    public static File createSubDir(String _subDirName) {
        if (TextUtils.isEmpty(_subDirName)) return null;
        File _file = new File(getSDCardFile(), appendBase(_subDirName));
        if (!_file.exists()) {
            _file.mkdirs();
        }
        return _file;
    }

    /**
     * 创建二级子目录下的文件，若二级目录没有创建，则创建
     *
     * @param _fileName
     * @return
     */
    public static File createSubDirFile(String _secondDirName, String _fileName) {
        if (TextUtils.isEmpty(_fileName) || TextUtils.isEmpty(_secondDirName)) return null;
        File _fileDir = new File(getSDCardFile(), appendBase(_secondDirName));
        if (!_fileDir.exists()) {
            _fileDir.mkdirs();
        }
        File _file = new File(getSDCardFile(), appendSecondBase(_secondDirName, _fileName));
        if (!_file.exists()) {
            try {
                _file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return _file;
    }

    /**
     * 在SD卡中创建自定义的文件目录，在一级目录下创建，millet/millet/...格式
     *
     * @param _customDir
     */
    public static void createCustomDir(String _customDir) {
        if (TextUtils.isEmpty(_customDir)) return;
        File _file = new File(getSDCardFile().getPath() + File.separator + _customDir);
        if (!_file.exists()) {
            _file.mkdirs();
        }
    }

    /**
     * 以根目录和文件名组合文件目录，二级目录
     *
     * @param _name
     * @returno
     */
    public static String appendBase(String _name) {
        return new StringBuffer(BASE_DIR).append("/").append(_name).toString();
    }

    /**
     * 以根目录和文件名组合文件目录，三级目录
     *
     * @param _secondDir
     * @returno
     */
    public static String appendSecondBase(String _secondDir, String _fileName) {
        return new StringBuffer(BASE_DIR).append("/").append(_secondDir).append("/").append(_fileName).toString();
    }

    /**
     * 获取SD卡的剩余容量 单位byte
     *
     * @return
     */
    public static long getSDCardAllSize() {
        if (isSDCardEnable()) {
            StatFs stat = new StatFs(getSDCardPath());
            //获取空闲的数据块的数量
            long availableBlocks = (long) stat.getAvailableBlocks() - 4;
            //获取单个数据块的大小（byte）
            long freeBlocks = stat.getAvailableBlocks();
            return freeBlocks * availableBlocks;
        }
        return 0;
    }

    /**
     * 获取指定路径所在空间的剩余可用容量字节数，单位byte
     *
     * @param filePath
     * @return 容量字节 SDCard可用空间，内部存储可用空间
     */
    public static long getFreeBytes(String filePath) {
        // 如果是sd卡的下的路径，则获取sd卡可用容量
        if (filePath.startsWith(getSDCardPath())) {
            filePath = getSDCardPath();
        } else {// 如果是内部存储的路径，则获取内存存储的可用容量
            filePath = Environment.getDataDirectory().getAbsolutePath();
        }
        StatFs stat = new StatFs(filePath);
        long availableBlocks = (long) stat.getAvailableBlocks() - 4;
        return stat.getBlockSize() * availableBlocks;
    }

}
