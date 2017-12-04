package com.millet.androidlib.Base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.millet.androidlib.UI.View.MaterialDialogHelper;

import java.util.Collection;

/**
 * Created by Administrator on 2017/1/12.
 */

public abstract class BaseActivity extends Activity {

    protected String TAG = this.getClass().getSimpleName();

    private Handler mUIHandler;
    private boolean isSoftShowing = false;
    private MaterialDialogHelper mMaterialDialogHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMaterialDialogHelper = new MaterialDialogHelper();
        initData(savedInstanceState);
        initView(savedInstanceState);
        loadData(savedInstanceState);
    }

    protected abstract void initData(Bundle savedInstanceState);

    protected abstract void initView(Bundle savedInstanceState);

    protected abstract void loadData(Bundle savedInstanceState);

    protected void showSoftKeyboard() {
        View view = getCurrentFocus();
        if (null == view)
            return;
        InputMethodManager imm = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        isSoftShowing = true;
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftKeyboard() {
        View view = getCurrentFocus();
        if (null == view)
            return;
        InputMethodManager imm = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        isSoftShowing = false;
    }

    public boolean isSoftShowing() {
        return isSoftShowing;
    }

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

    public void showProgress(String _mess) {
        mMaterialDialogHelper.showProgress(this, _mess);
    }

    public void dismissProgress() {
        mMaterialDialogHelper.dismissProgress();
    }

    public void showItemChooseSingle(Collection _collection, @NonNull MaterialDialog.ListCallback _callbac) {
        mMaterialDialogHelper.showItemChooseSingle(this, _collection, _callbac);
    }

}
