package com.millet.androidlib.Utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.millet.androidlib.Common.Constant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/1/19.
 * 用于应用Crash之后的捕获保存到SD卡，或者上传到服务器
 * 在Application中初始化
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static CrashHandler mCrashHandler = new CrashHandler();

    //系统默认的异常处理（默认情况下，系统会终止当前的异常程序）
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;

    private Context mContext;

    /**
     * 构造方法私有，防止外部创建多个实例，采用单例模式
     */
    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return mCrashHandler;
    }

    /**
     * 进行初始化工作
     *
     * @param _context
     */
    public void init(Context _context) {
        //获取系统默认的异常处理器
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        //将当前实例设为系统默认的异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        //获取Context，方便内部使用
        mContext = _context.getApplicationContext();
    }

    /**
     * 这个是最关键的函数，当程序中有未被捕获的异常，系统将会自动调用#uncaughtException方法
     * thread为出现未捕获异常的线程，e为未捕获的异常，有了这个e，我们就可以得到异常信息。
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        //导出异常到SD卡中
        pushExceptionToSDCard(e);
        //向服务器发送异常
        uploadExceptionToServer(e);
        //打印出当前的调用栈
        e.printStackTrace();
        //提示用户程序即将退出
        showToast(mContext, "很抱歉，程序遭遇异常，即将退出！");
        try {
            t.sleep(3000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        //如果系统提供了默认的异常处理器，则交给系统去结束我们的程序，否则就由我们自己结束自己
        if (null != mDefaultCrashHandler) {
            mDefaultCrashHandler.uncaughtException(t, e);
        } else {
            Process.killProcess(Process.myPid());
        }
    }

    private void pushExceptionToSDCard(Throwable _e) {
        //如果SD卡不存在或无法使用，则无法把异常信息写入SD卡
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (Constant.CrashHandler.DEBUG) {
                Log.w(Constant.CrashHandler.CONSTANT_TAG, "sdcard unmounted,skip push exception");
                return;
            }
        }
        File _dir = new File(Environment.getExternalStorageDirectory(), Constant.CrashHandler.CRASH_FILE_NAME);
        if (!_dir.exists()) {
            _dir.mkdirs();
        }
        long _currentTime = System.currentTimeMillis();
        String _timeFile = new SimpleDateFormat("yyyy-MM-dd").format(new Date(_currentTime));
        String _timeContent = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS").format(new Date(_currentTime));
        //以当前时间创建记录文件
        File _file = SDCardUtils.createSubDirFile(Constant.CrashHandler.CRASH_FILE_NAME, _timeFile + Constant.CrashHandler.CRASH_FILE_NAME_SUFFIX);
        try {
            PrintWriter _printWriter = new PrintWriter(new BufferedWriter(new FileWriter(_file, true)));
            //导出发生异常的时间
            _printWriter.println(_timeContent);
            //导出手机信息
            pushPhoneInfo(_printWriter);
            //导出异常的调用栈信息
            _e.printStackTrace(_printWriter);
            //关闭
            _printWriter.close();
        } catch (Exception e) {
            Log.e(Constant.CrashHandler.CONSTANT_TAG, "push crash info fail");
        }
    }

    private void pushPhoneInfo(PrintWriter _printWriter) throws PackageManager.NameNotFoundException {
        //获取应用的版本名称和版本号
        PackageManager _packageManager = mContext.getPackageManager();
        PackageInfo _packageInfo = _packageManager.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        _printWriter.print("App Version:");
        _printWriter.print(_packageInfo.versionName);
        _printWriter.print('_');
        _printWriter.println(_packageInfo.versionCode);

        //android版本号
        _printWriter.print("OS Version:");
        _printWriter.print(Build.VERSION.RELEASE);
        _printWriter.print('_');
        _printWriter.println(Build.VERSION.SDK_INT);

        //手机制造商
        _printWriter.print("Vendor:");
        _printWriter.println(Build.MANUFACTURER);

        //手机型号
        _printWriter.print("Model:");
        _printWriter.println(Build.MODEL);

        //手机cup架构
        _printWriter.print("CPU API:");
        _printWriter.println(Build.CPU_ABI);
    }

    /**
     * 上传至服务器
     *
     * @param _e
     */
    private void uploadExceptionToServer(Throwable _e) {

    }

    /**
     * 显示提示信息，需要在线程中显示Toast
     *
     * @param _context
     * @param _msg
     */
    private void showToast(final Context _context, final String _msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(_context, _msg, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }).start();
    }

}
