package com.millet.androidlib.Base;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.stetho.Stetho;
import com.millet.androidlib.Utils.CrashHandler;
import com.millet.androidlib.Utils.LogUtils;
import com.millet.androidlib.Utils.SDCardUtils;

/**
 * Created by Administrator on 2017/1/19.
 */

public class BaseApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化全局context
        mContext = getApplicationContext();

        //chrome调试
        Stetho.initializeWithDefaults(this);

        //初始化Fresco
        Fresco.initialize(this);

        //在这里为应用设置异常处理程序，然后我们的程序才能捕获未处理的异常
        CrashHandler _crashHandler = CrashHandler.getInstance();
        _crashHandler.init(this);

        //初始化文件
        SDCardUtils.initSDDir(getPackageName());
        LogUtils.configLog("xiaomi", "1", true, false, false, "log");

        //android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }

    }

    public static Context getInstance() {
        return mContext;
    }

}
