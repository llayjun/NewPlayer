package com.millet.androidlib.Function.TakePhoto;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.millet.androidlib.Base.BaseActivity;
import com.millet.androidlib.R;
import com.millet.androidlib.Utils.LogUtils;
import com.millet.androidlib.Utils.BitmapUtils;


/**
 * ClipImageActivity，截图片的界面
 * 2015年6月11日 下午11:29:55
 */
public class ClipImageActivity extends BaseActivity {

    public static final int REQUEST_CODE_CROP_PHOTO = 1002;

    //bundle key
    public static final String PRE_PATH = "PREPATH";//图片老地址
    public static final String NEW_PATH = "NEWPATH";//图片新地址
    public static final String SCALE = "SCALE";//缩放比例
    public static final double SCALE_SIZE = 1.0d;//缩放比例

    //UI
    private ClipImageLayout mClipImageLayout;

    private Bitmap mBitmap;
    private String mPrePath;
    private String mNewPath;
    private double mHWScale = 1.0d;
    private int screenWidth = 0;
    private int screenHeight = 0;

    public static void launch(Activity _activity, String _pathPre, String _pathNew) {
        Intent _intent = new Intent(_activity, ClipImageActivity.class);
        _intent.putExtra(PRE_PATH, _pathPre);
        _intent.putExtra(NEW_PATH, _pathNew);
        _intent.putExtra(SCALE, SCALE_SIZE);
        _activity.startActivityForResult(_intent, REQUEST_CODE_CROP_PHOTO);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_clip_image);
        ImageView left = (ImageView) findViewById(R.id.activity_clip_title_left);
        TextView right = (TextView) findViewById(R.id.activity_clip_title_right);
        init();
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.w("path:" + mNewPath);
                String path = mClipImageLayout.saveToLocal(mNewPath);
                Intent intent = new Intent();
                setResult(null == path ? RESULT_CANCELED : RESULT_OK, intent);
                if (null == path) {
                    Toast.makeText(getBaseContext(), R.string.photo_cut_too_small, Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    @Override
    protected void loadData(Bundle savedInstanceState) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBitmap != null) {
            mBitmap = null;
        }
    }

    private void init() {
        getWindowWH();
        mPrePath = getIntent().getStringExtra(PRE_PATH);
        mNewPath = getIntent().getStringExtra(NEW_PATH);
        mHWScale = getIntent().getDoubleExtra(SCALE, 1.0);
        mClipImageLayout = (ClipImageLayout) findViewById(R.id.id_clipImageLayout);
        mClipImageLayout.setHorizontalPadding(50);
        mClipImageLayout.setHWScale(mHWScale == 0 ? 1.0d : mHWScale);
        try {
            mBitmap = BitmapUtils.createBitmap(mPrePath, screenWidth, screenHeight);
            int width = 4 / 5 * (screenWidth > 540 ? 540 : screenWidth);
            if (mBitmap == null) {
                Toast.makeText(ClipImageActivity.this, R.string.no_photo, Toast.LENGTH_SHORT).show();
                finish();
            } else if (mBitmap.getWidth() < width) {
                Toast.makeText(ClipImageActivity.this, R.string.photo_select_too_small, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                resetImageView(mBitmap);
            }
        } catch (Exception e) {
            Toast.makeText(ClipImageActivity.this, R.string.no_photo, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * 获取屏幕的高和宽
     */
    private void getWindowWH() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }

    private void resetImageView(Bitmap b) {
        mClipImageLayout.clear();
        mClipImageLayout.setImageBitmap(b);
    }

}
