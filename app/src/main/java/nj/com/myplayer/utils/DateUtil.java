package nj.com.myplayer.utils;

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
     * @param beginTime
     * @param endTime
     * @return
     */
    public static long getTimeDifference(String beginTime,String endTime) {
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
     * @param timeString
     * @return
     */
    public static String timeFormat(String timeString) {
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateNowStr = sdf.format(new Date()) + " " + timeString;
            return dateNowStr;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
