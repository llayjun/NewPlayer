package com.millet.androidlib.Base;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static android.os.Looper.getMainLooper;

/**
 * Created by Administrator on 2017/1/12.
 */

public abstract class BaseFragment extends Fragment {

    protected String TAG = this.getClass().getSimpleName();

    private Handler mUIHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View _rootView = inflater.inflate(getViewId(), container, false);
        initData(container, savedInstanceState);
        initView(container, savedInstanceState, _rootView);
        loadData(container, savedInstanceState);
        return _rootView;
    }

    protected abstract int getViewId();

    protected abstract void initView(ViewGroup container, Bundle savedInstanceState, View _view);

    protected abstract void initData(ViewGroup container, Bundle savedInstanceState);

    protected abstract void loadData(ViewGroup container, Bundle savedInstanceState);

    /**
     * 获取UI线程中的Handler
     *
     * @return
     */
    public Handler getUIHandler() {
        if (null == mUIHandler) {
            mUIHandler = new Handler(getMainLooper());
        }
        return mUIHandler;
    }

}
