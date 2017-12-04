package com.millet.androidlib.Function.TakePhoto;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import com.millet.androidlib.Base.BaseActivity;
import com.millet.androidlib.Utils.BitmapUtils;
import com.millet.androidlib.Utils.LogUtils;
import com.millet.androidlib.Utils.SDCardUtils;

import java.io.File;

/**
 * 含有拍照裁剪和选择图片裁剪功能的Activity，子类继承此类
 * Created by Administrator on 2017/5/2 0002.
 */

public abstract class TakePhotoActivity extends BaseActivity {

    private static final Object TAG = TakePhotoActivity.class.getSimpleName();

    public static final int REQUEST_CODE_TAKE_CROP_PHOTO = 1001;
    public static final int REQUEST_CODE_CHOOSE_CROP_PHOTO = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showPhotoImage();
    }

    /**
     * 初始化头像
     */
    //该方法为初始化头像 mImageView.setImageBitmap(BitmapFactory.decodeFile(SDCardUtils.createFileByName("millet.jpg").getPath()));
    protected abstract void showPhotoImage();

    /**
     * 拍照成功
     */
    protected abstract void onTakePhotoSuccess();

    /**
     * 裁剪成功
     */
    protected abstract void onCropPhotoSuccess();

    /**
     * 选择图片成功
     */
    protected abstract void onChoosePhotoSuccess(Intent _data);

    /**
     * 复制图片成功
     */
    protected abstract void onCopyPhotoSuccess();

    /**
     * 跳转到拍照界面
     *
     * @param _cropPath 照片的名字
     */
    public void takePhotoAndCrop(String _cropPath) {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(_cropPath)));
            startActivityForResult(intent, REQUEST_CODE_TAKE_CROP_PHOTO);
        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
        }
    }

    /**
     * 裁剪图片
     *
     * @param _pathPre 原先地址
     * @param _pathNew 最新地址
     */
    public void cropPhoto(String _pathPre, String _pathNew) {
        try {
            ClipImageActivity.launch(this, _pathPre, _pathNew);
        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
        }
    }

    /**
     * 选择图片
     */
    public void choosePhotoAndCrop() {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/jpeg");
            startActivityForResult(intent, REQUEST_CODE_CHOOSE_CROP_PHOTO);
        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
        }
    }

    /**
     * 复制图片
     */
    public void copyPhoto(String _pathPre, String _pathNew) {
        try {
            if (!SDCardUtils.existFileByPath(_pathPre)) {
                Toast.makeText(this, "该图片不存在...", Toast.LENGTH_SHORT).show();
            } else {
                int _degree = BitmapUtils.readPictureDegree(_pathPre);
                Bitmap _bitmapPre = BitmapUtils.lessenUriImage(_pathPre, 720);
                Bitmap _bitmapNew = BitmapUtils.rotaingImageView(_degree, _bitmapPre);
                BitmapUtils.compressBitmapByQuality(_bitmapNew, _pathNew);
                onCopyPhotoSuccess();
            }
        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
        }
    }

    /**
     * 选择图片成功后，选择是否裁剪
     *
     * @param _data    选择图片后的data
     * @param _isCrop  是否裁剪
     * @param _pathNew 新的图片地址
     */
    public void chooseCropPhoto(Intent _data, boolean _isCrop, String _pathNew) {
        if (null == _data) return;
        Uri _uri = _data.getData();
        //这边data有时候数据拿不到
        if (!TextUtils.isEmpty(_uri.getAuthority())) {
            Cursor _cursor = getContentResolver().query(_uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
            if (null == _cursor) {
                Toast.makeText(this, "图片不存在...", Toast.LENGTH_SHORT).show();
            } else {
                _cursor.moveToFirst();
                String _path = _cursor.getString(_cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                if (_isCrop) {
                    cropPhoto(_path, _pathNew);
                } else {
                    copyPhoto(_path, _pathNew);
                }
            }
        } else {
            if (_isCrop) {
                cropPhoto(_uri.getPath(), _pathNew);
            } else {
                copyPhoto(_uri.getPath(), _pathNew);
            }
        }
    }

    /**
     * 在Activity中的Result，crop.jpg为拍照的图片，millet.jpg为裁剪后的图片，最终的头像
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_TAKE_CROP_PHOTO:
                onTakePhotoSuccess();
                //拍照成功
                break;
            case ClipImageActivity.REQUEST_CODE_CROP_PHOTO:
                //裁剪图片成功
                onCropPhotoSuccess();
                break;
            case REQUEST_CODE_CHOOSE_CROP_PHOTO:
                onChoosePhotoSuccess(data);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
