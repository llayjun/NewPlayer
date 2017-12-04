package com.millet.androidlib.Function.ChooseMultiplePhoto;

import android.app.Activity;
import android.content.Intent;

import com.millet.androidlib.Base.BaseActivity;
import com.millet.androidlib.Utils.LogUtils;

import java.util.ArrayList;

/**
 * 选择多个照片功能的Activity，子类继承此类
 * Created by Administrator on 2017/5/16 0002.
 */

public abstract class MultiplePhotoActivity extends BaseActivity {

    private static final Object TAG = MultiplePhotoActivity.class.getSimpleName();

    /**
     * 选择单个或者多个图片
     *
     * @param _chooseSize
     */
    public void chooseMultiplePhoto(int _chooseSize) {
        try {
            AlbumActivity.launch(this, _chooseSize);
        } catch (Exception _e) {
            LogUtils.e(_e.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case AlbumActivity.REQUEST_CODE_CHOOSE_PHOTO:
                ArrayList<String> _stringList = data.getStringArrayListExtra(AlbumDetailActivity.SELECTED_PIC);
                showMultipleChoosePic(_stringList);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public abstract void showMultipleChoosePic(ArrayList<String> _stringPathList);

}
