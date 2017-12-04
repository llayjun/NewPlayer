package com.millet.androidlib.EngineBase;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2017/1/12.
 * 用作处理复杂逻辑的基类
 */

public class EngineBase<T extends EngineBaseDelegate> {

    private Handler mHandler;

    private WeakReference<Context> mContext;

    private WeakReference<T> mDelegate;

    public EngineBase(Context _context) {
        this.setContext(_context);
    }

    public EngineBase(Context _context, T _t) {
        this.setContext(_context);
        this.setDelegate(_t);
    }

    private void setContext(Context _context) {
        if (null == _context) return;
        this.mContext = new WeakReference<Context>(_context);
    }

    private void setDelegate(T _t) {
        if (null == _t) return;
        this.mDelegate = new WeakReference<T>(_t);
    }

    public Context getContext() {
        return null == mContext ? null : mContext.get();
    }

    public T getDelegate() {
        return null == mDelegate ? null : mDelegate.get();
    }

    /**
     * Engine中获取主线程中的Handler
     *
     * @return 返回主线程中的Handler
     */
    protected Handler getUIHandler() {
        if (null == mHandler) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    /**
     * 想要在主线程中运行的Runnable
     *
     * @param _runnable 放进主线程中的部分
     */
    protected void runOnUiThread(Runnable _runnable) {
        Handler _handler;
        if (null == _runnable) return;
        _handler = getUIHandler();
        _handler.post(_runnable);
    }

}
