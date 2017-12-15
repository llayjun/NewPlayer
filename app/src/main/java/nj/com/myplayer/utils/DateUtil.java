package nj.com.myplayer.utils;

import android.app.AlarmManager;
import android.content.Context;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间工具类
 * Created by Administrator on 2017/11/28.
 */
public class DateUtil {

    /**
     * 计算两个时刻的时间差
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    public static long getTimeDifference(String beginTime, String endTime) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d1 = df.parse(beginTime);
            Date d2 = df.parse(endTime);
            long diff = d2.getTime() - d1.getTime();
            return diff;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 时间格式转换
     *
     * @param timeString
     * @return
     */
    public static String timeFormat(String timeString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateNowStr = sdf.format(new Date()) + " " + timeString;
            return dateNowStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 请求root权限
     *
     * @return 应用程序是/否获取Root权限
     */
    public static boolean requestRootPermission(Context _context, String pkgCodePath) {
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd = "chmod 777 " + pkgCodePath;
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            Toast.makeText(_context, "权限不足", Toast.LENGTH_SHORT).show();
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }

    /**
     * 设置时区
     *
     * @param _context
     */
    public static void setTimeZone(Context _context) {
        AlarmManager mAlarmManager = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.setTimeZone("GMT+09:00");
    }

    /**
     * 设置系统时间
     *
     * @param time 格式为“年月日.时分秒”，例如：20111209.121212
     */
    public static boolean setTime(String time) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("date -s " + time + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }

}
