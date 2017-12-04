package com.millet.androidlib.UI.View;

import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Collection;

/**
 * Created by Administrator on 2017/9/14 0014.
 */

public class MaterialDialogHelper {

    MaterialDialog.Builder mBuilder;
    MaterialDialog mMaterialDialog;


    /**
     * 显示等待框
     *
     * @param _context
     * @param _message
     */
    public void showProgress(Context _context, String _message) {
        mBuilder = null;
        mBuilder = new MaterialDialog.Builder(_context);
        mBuilder.progress(false, 0);
        mBuilder.content(_message);
        mMaterialDialog = mBuilder.build();
        mMaterialDialog.show();
    }

    /**
     * 取消Dialog
     */
    public void dismissProgress() {
        if (null != mMaterialDialog && mMaterialDialog.isShowing() && null != mMaterialDialog.getWindow()) {
            mMaterialDialog.dismiss();
        }
        mMaterialDialog = null;
    }

    /**
     * 显示Item然后进行选择
     *
     * @param _context
     * @param _collection
     */
    public void showItemChooseSingle(Context _context, Collection _collection, @NonNull MaterialDialog.ListCallback _callback) {
        mBuilder = null;
        mBuilder = new MaterialDialog.Builder(_context);
        mBuilder.items(_collection);
        mBuilder.autoDismiss(true);
        mBuilder.itemsCallback(_callback);
        mMaterialDialog = mBuilder.build();
        mMaterialDialog.show();
    }

}
