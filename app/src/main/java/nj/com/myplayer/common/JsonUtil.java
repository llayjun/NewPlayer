package nj.com.myplayer.common;


import com.google.gson.Gson;

import java.lang.reflect.Type;

/* *
 * Java对象和JSON字符串相互转化工具类
 * @author Administrator
 *
 */

public final class JsonUtil {

    private JsonUtil() {
    }

    private static Gson gson = new Gson();

    /**
     * 对象转换成json字符串
     *
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        try {
            return gson.toJson(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * json字符串转成对象
     *
     * @param str
     * @param type
     * @return
     */
    public static <T> T fromJson(String str, Type type) {
        try {
            return gson.fromJson(str, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * json字符串转成对象
     *
     * @param str
     * @param type
     * @return
     */
    public static <T> T fromJson(String str, Class<T> type) {
        try {
            return gson.fromJson(str, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

