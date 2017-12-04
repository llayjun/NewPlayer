package com.millet.androidlib.Common;

/**
 * Created by Administrator on 2017/1/20.
 * 一些常用的常量
 */

public class Constant {
    /**
     * 一些开关上的设置
     */
    public class Config {
        public static final boolean DEBUG_MODE = true;
    }

    public class LogConfig {
        //日志文件名
        public static final String LOG_NAME = "log.txt";
        //Catch捕获的异常文件名
        public static final String CATCH_NAME = "catch.txt";
    }

    /**
     * CrashHandler的常量
     */
    public class CrashHandler {
        public static final boolean DEBUG = Config.DEBUG_MODE;
        public static final String CONSTANT_TAG = "CrashHandler";
        //存放在SD卡中文件名字
        public static final String CRASH_FILE_NAME = "crash";
        //存放在SD卡中文件后缀
        public static final String CRASH_FILE_NAME_SUFFIX = ".trace";
    }

    /**
     * OkHttpManager的常量
     */
    public class OkHttpManager {
        public static final boolean DEBUG = Config.DEBUG_MODE;
        public static final String CONSTANT_TAG = "OkHttpManager";
    }

}
