package com.millet.androidlib.Utils;

import android.content.pm.PackageManager;
import android.util.Log;

import com.millet.androidlib.Common.Constant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2017/5/2 0002.
 */

public class LogUtils {
    public static String TAG = "millet_debug";
    public static String VERSION = "1";
    public static boolean LOG_DEGUG = false;
    public static boolean LOG_TO_FILE = false;
    public static boolean CATCH_TO_FILE = false;
    public static String LOG_DIR = "second";


    public static void configLog(String _tag, String _version, boolean _debug, boolean _fileLog, boolean _catchLog, String _dir) {
        TAG = _tag;//显示的日志TAG
        VERSION = _version;//日志版本
        LOG_DEGUG = _debug;//是否是debug模式，正式版关闭
        LOG_TO_FILE = _fileLog;//日志是否保存到文件中
        CATCH_TO_FILE = _catchLog;//catch是否保存到文件中
        LOG_DIR = _dir;//保存到文件的路径
    }

    /**
     * 打印日志v
     *
     * @param _info
     */
    public static void v(String _info) {
        if (LOG_DEGUG) {
            Log.v(TAG, _info);
            logToSdCard(_info);
        }
    }

    /**
     * 打印日志d
     *
     * @param _info
     */
    public static void d(String _info) {
        if (LOG_DEGUG) {
            Log.d(TAG, _info);
            logToSdCard(_info);
        }
    }

    /**
     * 打印日志i
     *
     * @param _info
     */
    public static void i(String _info) {
        if (LOG_DEGUG) {
            Log.i(TAG, _info);
            logToSdCard(_info);
        }
    }

    /**
     * 打印日志w
     *
     * @param _info
     */
    public static void w(String _info) {
        if (LOG_DEGUG) {
            Log.w(TAG, _info);
            logToSdCard(_info);
        }
    }

    /**
     * 打印日志e
     *
     * @param _info
     */
    public static void e(String _info) {
        if (LOG_DEGUG) {
            Log.e(TAG, _info);
            logToSdCard(_info);
        }
    }

    /**
     * 保存catch异常到SD卡
     *
     * @param _catchInfo
     */
    public static void catchInfo(String _catchInfo) {
        if (LOG_DEGUG) {
            Log.e(TAG, _catchInfo);
            catchToSdCard(_catchInfo);
        }
    }

    /**
     * 保存log到SD卡
     */
    private static void logToSdCard(String _text) {
        if (LOG_TO_FILE) {
            File _file = SDCardUtils.createSubDirFile(LOG_DIR, Constant.LogConfig.LOG_NAME);
            writeLogToSDCard(_file, _text);
        }
    }

    /**
     * catch捕获的异常存到文件中
     *
     * @param _text
     */
    private static void catchToSdCard(String _text) {
        if (CATCH_TO_FILE) {
            File _file = SDCardUtils.createSubDirFile(LOG_DIR, Constant.LogConfig.CATCH_NAME);
            writeCatchToSDCard(_file, _text);
        }
    }

    /**
     * 把Log文字写到文件中去，显示时间
     *
     * @param _file
     * @param _text
     */
    public static void writeLogToSDCard(File _file, String _text) {
        FileOutputStream _fileOutputStream = null;
        try {
            _fileOutputStream = new FileOutputStream(_file, true);
            _fileOutputStream.write(("[" + DateUtils.formatYMDHMS(DateUtils.currentTime()) + "]" + "\n").getBytes());
            _fileOutputStream.write((_text + "\n").getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != _fileOutputStream) {
                try {
                    _fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 把Catch文字写到文件中去，显示时间,机型信息等
     *
     * @param _file
     * @param _text
     */
    public static void writeCatchToSDCard(File _file, String _text) {
        FileOutputStream _fileOutputStream = null;
        try {
            _fileOutputStream = new FileOutputStream(_file, true);
            _fileOutputStream.write(("[" + DateUtils.formatYMDHMS(DateUtils.currentTime()) + "]" + "\n").getBytes());
            DeviceUtils.pushPhoneInfo(_fileOutputStream);
            _fileOutputStream.write((_text + "\n").getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (null != _fileOutputStream) {
                try {
                    _fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
