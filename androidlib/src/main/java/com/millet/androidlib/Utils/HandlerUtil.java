package com.millet.androidlib.Utils;

import android.os.Handler;

public class HandlerUtil extends Handler {

    private static HandlerUtil instance;

    public static synchronized Handler getMainHandler() {
        if (null == instance) {
            instance = new HandlerUtil();
        }
        return instance;
    }

    private HandlerUtil() {

    }
}
