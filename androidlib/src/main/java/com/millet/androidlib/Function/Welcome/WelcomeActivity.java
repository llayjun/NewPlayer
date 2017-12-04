package com.millet.androidlib.Function.Welcome;

import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;

import com.millet.androidlib.Base.BaseActivity;
import com.millet.androidlib.Function.BootPage.BootPageActivity;
import com.millet.androidlib.R;
import com.millet.androidlib.Utils.DeviceUtils;
import com.millet.androidlib.Utils.SPUtils;

/**
 * 闪屏页，点击可以加快进入
 * Created by Administrator on 2017/5/12 0012.
 */

public class WelcomeActivity extends BaseActivity {

    private static final int DELAY_TIME = 5000;//闪屏页的时间

    private Handler mHandler = new Handler();
    private Runnable mCallBack;

    @Override
    protected void initData(Bundle savedInstanceState) {
        mCallBack = new Runnable() {
            @Override
            public void run() {
                login();
            }
        };
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_welcome);
    }

    @Override
    protected void loadData(Bundle savedInstanceState) {
        mHandler.postDelayed(mCallBack, DELAY_TIME);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            login();
            if (null != mHandler) {
                mHandler.removeCallbacks(mCallBack);
            }
        }
        return super.onTouchEvent(event);
    }

    private void login() {
        String _oldName = (String) SPUtils.getDataFromSp(this, BootPageActivity.BOOT_KEY, "");
        String _newName = DeviceUtils.getAppVersionName(this);
        if (_oldName.equals(_newName)) {
            //跳转到登录
        } else {
            //跳转到启动页
            BootPageActivity.launch(this);
        }
        finish();
    }

}
