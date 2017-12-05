package nj.com.myplayer.common;

import java.util.Collection;
import java.util.Map;

/**
 * <Description>对象校验工具类 <br>
 *
 * @author CaoYK<br>
 * @version 1.0<br>
 * @CreateDate 2017年04月21日 <br>
 */
public final class ObjectUtils {

    /**
     * ObjectUtils 私有构造函数
     */
    private ObjectUtils() {

    }

    /**
     * 判断对象是否为空
     *
     * @param obj 入参对象
     * @return 对象为空返回true 反之亦然
     */
    public static boolean isNullOrEmpty(Object obj) {
        if (null == obj) {
            return true;
        }
        if (obj instanceof String) {
            return ((String) obj).trim().length() == 0;
        }
        if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length() == 0;
        }
        if (obj instanceof Collection) {
            return ((Collection<?>) obj).isEmpty();
        }
        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).isEmpty();
        }
        if (obj instanceof Object[]) {
            Object[] object = (Object[]) obj;
            if (object.length == 0) {
                return true;
            }
            boolean empty = true;
            for (int i = 0; i < object.length; i++) {
                if (!isNullOrEmpty(object[i])) {
                    empty = false;
                    break;
                }
            }
            return empty;
        }
        return false;
    }

    public static void main(String[] args) {


    }


}
