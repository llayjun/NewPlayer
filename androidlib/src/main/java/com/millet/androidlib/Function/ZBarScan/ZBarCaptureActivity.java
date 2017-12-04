package com.millet.androidlib.Function.ZBarScan;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dtr.zbar.build.ZBarDecoder;
import com.facebook.stetho.common.LogUtil;
import com.millet.androidlib.Function.ZBarScan.scan.BeepManager;
import com.millet.androidlib.Function.ZBarScan.scan.InactivityTimer;
import com.millet.androidlib.Function.ZBarScan.scan.ZBarCameraManager;
import com.millet.androidlib.R;
import com.millet.androidlib.Utils.BitmapUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * 扫描二维码功能界面，和BitmapUtils.createQRImage("");结合起来使用
 */
public class ZBarCaptureActivity extends Activity implements SurfaceHolder.Callback, View.OnClickListener {

    private static final String TAG = ZBarCaptureActivity.class.getSimpleName();

    public static void startLaunchForResult(Activity _activity, int _requestCode) {
        Intent _intent = new Intent(_activity, ZBarCaptureActivity.class);
        _activity.startActivityForResult(_intent, _requestCode);
    }

    public static final int REQUEST_RECODE = 200;//请求码
    public static final String RESULT_KEY = "result";//请求结果

    public static final int MESSAGE_SUCCESS = 1;
    public static final int MESSAGE_FAILURE = 2;

    // private Camera mCamera;
    private ZBarCaptureActivityHandler handler;
    private Handler autoFocusHandler;
    private ZBarCameraManager mCameraManager;

    private SurfaceView scanPreview;
    private RelativeLayout scanContainer;
    private RelativeLayout scanCropView;
    private ImageView scanLine;
    private ImageView mFlash;

    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;

    private Rect mCropRect = null;
    private boolean previewing = false;

    private boolean isHasSurface = false;

    private ZBarDecoder zBarDecoder = new ZBarDecoder();
    protected SystemBarTintManager tintManager;
    private boolean init = true;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initWindow();
        init = true;
        setContentView(R.layout.activity_qr_scan);
        scanPreview = (SurfaceView) findViewById(R.id.capture_preview);
        scanContainer = (RelativeLayout) findViewById(R.id.capture_container);
        scanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
        scanLine = (ImageView) findViewById(R.id.capture_scan_line);
        mFlash = (ImageView) findViewById(R.id.capture_flash);
        mFlash.setOnClickListener(this);

        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);

        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.9f);
        animation.setDuration(3500);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.REVERSE);
        scanLine.startAnimation(animation);
    }

    @TargetApi(19)
    protected void initWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(0xFF555555);
            tintManager.setStatusBarTintEnabled(true);
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        LogUtil.d("initCamera start");
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (mCameraManager.isOpen()) {
            Log.w(TAG,
                    "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            mCameraManager.openDriver();
            if (handler == null) {
                handler = new ZBarCaptureActivityHandler(this, mCameraManager);
            }
            // mCameraManager.getCamera().setDisplayOrientation(90);//没必要
            mCameraManager.getCamera().setPreviewDisplay(surfaceHolder);
            mCameraManager.getCamera().setPreviewCallback(previewCb);
            mCameraManager.getCamera().startPreview();
            mCameraManager.getCamera().autoFocus(autoFocusCB);
            previewing = true;
            initCrop();
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
        LogUtil.d("initCamera end");
    }

    private void displayFrameworkBugMessageAndExit() {
        // camera error
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage("相机打开出错，请稍后重试");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }

        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
         * 处理initCarmer 比较耗时，所以等10ms先让界面显示出来。
         */
        if (init) {
            init = false;
            delayHandle.sendEmptyMessageDelayed(0, 40);
        } else {
            resumePreView();
        }
    }

    Handler delayHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            resumePreView();
        }
    };

    private void resumePreView() {
        LogUtil.d("resumePreView start");
        scanPreview.setVisibility(View.VISIBLE);
        autoFocusHandler = new Handler();
        // CameraManager must be initialized here, not in onCreate(). This is
        // necessary because we don't
        // want to open the camera driver and measure the screen size if we're
        // going to show the help on
        // first launch. That led to bugs where the scanning rectangle was the
        // wrong size and partially
        // off screen.
        mCameraManager = new ZBarCameraManager(getApplication());

        handler = null;

        if (isHasSurface) {
            // The activity was paused but not stopped, so the surface still
            // exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(scanPreview.getHolder());
        } else {
            // Install the callback and wait for surfaceCreated() to init the
            // camera.
            LogUtil.d("addCallback");
            scanPreview.getHolder().addCallback(this);
        }
        inactivityTimer.onResume();
        LogUtil.d("resumePreView end");
    }

    public void onPause() {
        releaseCamera();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        beepManager.close();
        mCameraManager.closeDriver();
        if (!isHasSurface) {
            scanPreview.getHolder().removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        releaseCamera();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.shutdown();
        beepManager.close();
        mCameraManager.closeDriver();
        if (!isHasSurface) {
            scanPreview.getHolder().removeCallback(this);
        }
        super.onDestroy();
    }

    private void releaseCamera() {
        if (null != mCameraManager && mCameraManager.getCamera() != null
                && previewing) {
            previewing = false;
            mCameraManager.getCamera().setPreviewCallback(null);
            mCameraManager.getCamera().cancelAutoFocus();
            mCameraManager.getCamera().stopPreview();
            mCameraManager.getCamera().release();
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing) {
                mCameraManager.getCamera().autoFocus(autoFocusCB);
            }
        }
    };

    PreviewCallback previewCb = new PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            if (!previewing) {
                return;
            }
            Size size = camera.getParameters().getPreviewSize();

            // 这里需要将获取的data翻转一下，因为相机默认拿的的横屏的数据
            byte[] rotatedData = new byte[data.length];
            for (int y = 0; y < size.height; y++) {
                for (int x = 0; x < size.width; x++) {
                    rotatedData[x * size.height + size.height - y - 1] = data[x
                            + y * size.width];
                }
            }

            // 宽高也要调整
            int tmp = size.width;
            size.width = size.height;
            size.height = tmp;

            // initCrop();
            // ZBarDecoder zBarDecoder = new ZBarDecoder();
            String result = zBarDecoder.decodeCrop(rotatedData, size.width,
                    size.height, mCropRect.left, mCropRect.top,
                    mCropRect.width(), mCropRect.height());

            if (!TextUtils.isEmpty(result)) {
                previewing = false;
                mCameraManager.getCamera().setPreviewCallback(null);
                mCameraManager.getCamera().stopPreview();

                if (handler != null) {
                    Message msg = handler.obtainMessage();
                    msg.what = MESSAGE_SUCCESS;
                    msg.obj = result;
                    handler.sendMessage(msg);
                }

                // scanResult.setText("barcode result " + result);
                LogUtil.d("barcode result " + result);
            }
        }
    };

    // Mimic continuous auto-focusing
    AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 200);
        }
    };

    /**
     * 初始化截取的矩形区域
     */
    private void initCrop() {
        int cameraWidth = mCameraManager.getCameraResolution().y;
        int cameraHeight = mCameraManager.getCameraResolution().x;

        /** 获取布局中扫描框的位置信息 */
        int[] location = new int[2];
        scanCropView.getLocationInWindow(location);

        int cropLeft = location[0];
        int cropTop = location[1] - getStatusBarHeight();

        int cropWidth = scanCropView.getWidth();
        int cropHeight = scanCropView.getHeight();

        /** 获取布局容器的宽高 */
        int containerWidth = scanContainer.getWidth();
        int containerHeight = scanContainer.getHeight();

        /** 计算最终截取的矩形的左上角顶点x坐标 */
        int x = cropLeft * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的左上角顶点y坐标 */
        int y = cropTop * cameraHeight / containerHeight;

        /** 计算最终截取的矩形的宽度 */
        int width = cropWidth * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的高度 */
        int height = cropHeight * cameraHeight / containerHeight;

        /** 生成最终的截取的矩形 */
        mCropRect = new Rect(x, y, width + x, height + y);
    }

    private int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.capture_flash) {
            light();
        }
    }

    private boolean flag;

    protected void light() {

        if (!checkoutHasFlash())
            return;
        if (flag == true) {
            flag = false;
            // 开闪光灯
            mCameraManager.openLight();
            mFlash.setBackgroundResource(R.mipmap.flash_open);
        } else {
            flag = true;
            // 关闪光灯
            mCameraManager.offLight();
            mFlash.setBackgroundResource(R.mipmap.flash_default);
        }
    }

    /**
     * 检查设备闪光灯
     */
    private boolean checkoutHasFlash() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtil.d("surfaceCreated start");
        if (!isHasSurface) {
            isHasSurface = true;
            initCamera(holder);
        }
        LogUtil.d("surfaceCreated end");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isHasSurface = false;
    }

    public void onTitleLeftClick(View v) {
        finish();
    }

    public void handleDecode(final String result) {
        inactivityTimer.onActivity();
        beepManager.playBeepSoundAndVibrate();

        // 通过这种方式可以获取到扫描的图片
        // bundle.putInt("width", mCropRect.width());
        // bundle.putInt("height", mCropRect.height());
        // bundle.putString("result", rawResult.getText());
        //
        // startActivity(new Intent(CaptureActivity.this, ResultActivity.class)
        // .putExtras(bundle));

        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                handleText(result);
            }
        }, 100);
    }

    private void handleText(String text) {
        LogUtil.d(TAG, "----------------- " + text);
        Intent i = new Intent();
        i.putExtra(RESULT_KEY, text);
        this.setResult(RESULT_OK, i);
        finish();
    }

    private class ZBarCaptureActivityHandler extends Handler {
        private final ZBarCaptureActivity activity;
        private final ZBarCameraManager cameraManager;

        public ZBarCaptureActivityHandler(ZBarCaptureActivity activity,
                                          ZBarCameraManager cameraManager) {
            this.activity = activity;
            // Start ourselves capturing previews and decoding.
            this.cameraManager = cameraManager;

        }

        @Override
        public void handleMessage(Message message) {
            if (message.what == MESSAGE_SUCCESS) {
                activity.handleDecode((String) message.obj);
            }
            // else if(message.what == R.id.return_scan_result)
            // {
            // activity.setResult(Activity.RESULT_OK, (Intent) message.obj);
            // activity.finish();
            // }
        }

        public void quitSynchronously() {
            // mCameraManager.getCamera().cancelAutoFocus();
            // cameraManager.getCamera().stopPreview();
            // Be absolutely sure we don't send any queued up messages
            removeMessages(MESSAGE_SUCCESS);
            removeMessages(MESSAGE_FAILURE);
        }
    }
}
