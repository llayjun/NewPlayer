package com.millet.androidlib.Utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by qjm201000 on 2017/5/19.
 */
public class ToastUtils {
    private static Toast mToast = null;

    /**
     * 防止连续按时，多个toast排队显示
     *
     * @param context
     * @param text
     * @param duration
     */
    public static void showToast(Context context, String text, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context, text, duration);
        } else {
            mToast.setText(text);
            mToast.setDuration(duration);
        }
        mToast.show();
    }
}
