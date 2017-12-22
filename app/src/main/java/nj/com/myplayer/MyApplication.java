package nj.com.myplayer;

import android.content.Context;

import com.millet.androidlib.Base.BaseApplication;
import com.millet.androidlib.Utils.SharedPreferencesHelper;
import com.tencent.bugly.crashreport.CrashReport;

import nj.com.myplayer.utils.BuglyUtil;

/**
 * Created by Administrator on 2017/12/11 0011.
 */

public class MyApplication extends BaseApplication {

    public static final String BUGLY_APPID = "e6497dc975";
    public static final boolean BUGLY_DEBUG = true;//建议在测试阶段建议设置成true，发布时设置为false。

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = getApplicationContext();
        // 获取当前包名
        String packageName = context.getPackageName();
        // 获取当前进程名
        String processName = BuglyUtil.getProcessName(android.os.Process.myPid());
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        //bugly初始化
        CrashReport.initCrashReport(getApplicationContext(), BUGLY_APPID, BUGLY_DEBUG, strategy);
        SharedPreferencesHelper.getInstance();
        SharedPreferencesHelper.getInstance();
    }

}
