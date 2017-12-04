package com.millet.androidlib.Function.BootPage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.millet.androidlib.Base.BaseActivity;
import com.millet.androidlib.R;
import com.millet.androidlib.Utils.DeviceUtils;
import com.millet.androidlib.Utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/12 0012.
 */

public class BootPageActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private static final String TAG = BootPageActivity.class.getSimpleName();

    public static final String BOOT_KEY = "boot_key";

    public static void launch(Activity _activity) {
        Intent _intent = new Intent(_activity, BootPageActivity.class);
        _activity.startActivity(_intent);
    }

    private int[] mPicId = {R.mipmap.bootpage1, R.mipmap.bootpage2, R.mipmap.bootpage3};

    //UI
    private ViewPager mViewPager;
    private ImageView mImageJump;
    private ImageView mImageEnjoy;
    private LinearLayout mLinearPoint;

    //data
    private List<View> mView = new ArrayList<>();

    @Override
    protected void initData(Bundle savedInstanceState) {
        //初始化View列表
        ViewGroup.LayoutParams _param = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        for (int i = 0; i < mPicId.length; i++) {
            ImageView _imageView = new ImageView(this);
            _imageView.setLayoutParams(_param);
            _imageView.setImageResource(mPicId[i]);
            _imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            mView.add(_imageView);
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_boot_page);
        mImageJump = (ImageView) findViewById(R.id.activity_boot_page_jump);
        mImageJump.setOnClickListener(this);
        mImageEnjoy = (ImageView) findViewById(R.id.activity_boot_page_enjoy);
        mImageEnjoy.setOnClickListener(this);
        mLinearPoint = (LinearLayout) findViewById(R.id.activity_boot_page_point);
        mViewPager = (ViewPager) findViewById(R.id.activity_boot_page_view_pager);
    }

    @Override
    protected void loadData(Bundle savedInstanceState) {
        BootPageAdapter _adapter = new BootPageAdapter(mView);
        mViewPager.setAdapter(_adapter);
        mViewPager.setOnPageChangeListener(this);
        updatePoint(0);
    }

    @Override
    public void onClick(View v) {
        int _id = v.getId();
        if (_id == R.id.activity_boot_page_jump || _id == R.id.activity_boot_page_enjoy) {
            //保存到sp中
            SPUtils.saveDataToSp(this, BOOT_KEY, DeviceUtils.getAppVersionName(this));
            finish();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        updatePoint(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 更新底部的小红点
     *
     * @param _position
     */
    private void updatePoint(int _position) {
        if (null == mLinearPoint) return;
        for (int i = 0; i < mLinearPoint.getChildCount(); i++) {
            View _view = mLinearPoint.getChildAt(i);
            if (_position == i) {
                _view.setSelected(true);
            } else {
                _view.setSelected(false);
            }
        }
        if (_position == mView.size() - 1) {
            mImageEnjoy.setVisibility(View.VISIBLE);
        } else {
            mImageEnjoy.setVisibility(View.GONE);
        }
    }

}
