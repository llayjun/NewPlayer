package com.millet.androidlib.Function.ZoomImage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.millet.androidlib.Base.BaseActivity;
import com.millet.androidlib.R;
import com.millet.androidlib.UI.CustomView.ZoomsImageView;
import com.millet.androidlib.Utils.GlideUtils;

/**
 * Created by Administrator on 2017/9/15 0015.
 */

public class ZoomImageActivity extends BaseActivity {

    public static void launch(Context _context, String _str) {
        Intent _intent = new Intent(_context, ZoomImageActivity.class);
        _intent.putExtra(STRING_PATH, _str);
        _context.startActivity(_intent);
    }

    private static final String STRING_PATH = "STRING_PATH";

    private ZoomsImageView mZoomImageView;
    private ImageView mImageViewBack;

    private String mString;


    @Override
    protected void initData(Bundle savedInstanceState) {
        Intent _intent = getIntent();
        if (null != _intent) {
            if (_intent.hasExtra(STRING_PATH)) {
                mString = _intent.getStringExtra(STRING_PATH);
            }
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_zoom_image_view);
        mZoomImageView = (ZoomsImageView) findViewById(R.id.zoom_image_view);
        mImageViewBack = (ImageView) findViewById(R.id.zooms_image_back);
        if (!TextUtils.isEmpty(mString)) {
            GlideUtils.loadImageView(this, mString, mZoomImageView);
        }
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                ZoomImageActivity.this.finish();
            }
        });
    }

    @Override
    protected void loadData(Bundle savedInstanceState) {

    }

}
