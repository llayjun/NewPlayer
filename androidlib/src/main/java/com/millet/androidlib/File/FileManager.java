package com.millet.androidlib.File;

import android.content.Context;
import android.media.ExifInterface;

import com.millet.androidlib.Utils.SDCardUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2017/1/20.
 */

public class FileManager {

    /**
     * 保存string到Cache中去
     *
     * @param _context  上下文
     * @param _fileName 保存的cache文件名字
     * @param _content  String内容
     * @return 是否保存成功
     */
    public static boolean saveStringToCache(Context _context, String _fileName, String _content) {
        boolean _flag = false;
        FileOutputStream _fileOutputStream = null;
        File _file = getDiskCacheDir(_context, _fileName);
        if (null == _file || null == _content) return _flag;
        try {
            _fileOutputStream = new FileOutputStream(_file);
            _fileOutputStream.write(_content.getBytes());
            _flag = true;
        } catch (IOException e) {
            e.printStackTrace();
            _flag = false;
        } finally {
            try {
                _fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return _flag;
    }

    /**
     * 从Cache中取出内容
     *
     * @param _context  上下文
     * @param _fileName 文件的名字
     * @return
     */
    public static String getStringFromCache(Context _context, String _fileName) {
        FileInputStream _fileInputStream = null;
        ByteArrayOutputStream _byteArrayOutputStream = null;
        File _file = getDiskCacheDir(_context, _fileName);
        if (null == _file || null == _fileName) return null;
        try {
            _fileInputStream = new FileInputStream(_file);
            _byteArrayOutputStream = new ByteArrayOutputStream();
            int _len = 0;
            byte[] _byte = new byte[1024];
            try {
                while (-1 != (_len = _fileInputStream.read(_byte))) {
                    _byteArrayOutputStream.write(_byte, 0, _len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                _byteArrayOutputStream.close();
                _fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new String(_byteArrayOutputStream.toByteArray());
    }

    /**
     * 获取缓存路径，存储临时文件，可被一键清理和卸载清理
     * 可以看到，当SD卡存在或者SD卡不可被移除的时候，
     * 就调用getExternalCacheDir()方法来获取缓存路径，
     * 否则就调用getCacheDir()方法来获取缓存路径。
     * 前者获取到的就是/sdcard/Android/data/<application package>/cache/ 这个路径，
     * 而后者获取到的是/data/data/<application package>/cache/ 这个路径。
     * 注意区别Environment.getExternalStorageDirectory()，/storage/emulated/0这个路径
     *
     * @param _context
     * @param _fileName
     * @return
     */
    public static File getDiskCacheDir(Context _context, String _fileName) {
        String _cachePath;
        if (SDCardUtils.isSDCardEnable()) {
            //SD卡中的缓存地址
            _cachePath = _context.getExternalCacheDir().getPath();
        } else {
            //内部中的缓存地址
            _cachePath = _context.getCacheDir().getPath();
        }
        return new File(_cachePath + File.separator + _fileName);
    }


}
