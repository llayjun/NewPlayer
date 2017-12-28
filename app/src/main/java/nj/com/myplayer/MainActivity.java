package nj.com.myplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.millet.androidlib.Base.BaseActivity;
import com.millet.androidlib.Net.ExecutorManager;
import com.millet.androidlib.Utils.DateUtils;
import com.millet.androidlib.Utils.GlideUtils;
import com.millet.androidlib.Utils.LogUtils;
import com.millet.androidlib.Utils.TextUtils;
import com.millet.androidlib.Utils.ToastUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import nj.com.myplayer.common.Constant;
import nj.com.myplayer.common.FileAnalyzeUtil;
import nj.com.myplayer.listener.BatteryListener;
import nj.com.myplayer.listener.BatteryStateListener;
import nj.com.myplayer.model.PlayerBean;
import nj.com.myplayer.model.TextBean;
import nj.com.myplayer.service.LocalService;
import nj.com.myplayer.utils.DateUtil;
import nj.com.myplayer.utils.SPPlayerHelper;
import nj.com.myplayer.utils.SPRollHelper;
import nj.com.myplayer.utils.ScreenUtil;

public class MainActivity extends BaseActivity implements PlayerManager.PlayerStateListener, Runnable {

    private File mFile = new File(Environment.getExternalStorageDirectory(), Constant.FILE_PATH);

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;//动态获取权限

    //广播监听电量状态
    private BatteryListener mBatteryListener;
    //UI
    //视屏
    private PlayerManager mPlayerManager;
    //图片
    private ImageView mImageView;
    //danmmu
    private BaseDanmakuParser mBaseDanmakuParser;//解析弹幕
    private IDanmakuView mIDanmakuView;//弹幕view
    private DanmakuContext mContext;
    //time
    private TextView mTime;
    //screen image
    private ImageView mScreenImage;

    //data
    //设置弹幕的最大显示行数
    private HashMap<Integer, Integer> maxLinesPair;
    //设置是否禁止重叠
    private HashMap<Integer, Boolean> overlappingEnablePair;
    private ImageHandler mImageHandler = new ImageHandler();
    private int mIndex = 0;//记录已经播放到第几个了
    //准备播放的列表
    private List<PlayerBean.PlayerInfo> mNowPlayInfoList = new ArrayList<>();
    private PlayerBean.PlayTimeInfo mPlayerTime;
    //播放的字幕
    private TextBean mTextBean;//正在播放的文字
    private int mShowingTimes = 0;//播放次数

    @Override
    protected void initData(Bundle savedInstanceState) {
        try {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            ScreenUtil.hideBottomUIMenu(MainActivity.this);
            if (Build.VERSION.SDK_INT > 23) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                } else {
                    loadFile();
                }
            } else {
                loadFile();
            }
        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
        }
    }

    public void loadFile() {
        if (mFile.exists()) {
            FileAnalyzeUtil.savePlayInfo2Shared(mFile.getPath());
            FileAnalyzeUtil.saveRollTextInfo2Shared(this, mFile.getPath());
            ToastUtils.showToast(this, "文件扫描成功", Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the task you need to do.
                    loadFile();
                } else {
                    ToastUtils.showToast(MainActivity.this, "请打开读取文件权限", Toast.LENGTH_LONG);
                }
            }
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        try {
            setContentView(R.layout.activity_main);
            startService(new Intent(this, LocalService.class));
//            startService(new Intent(this, RemoteService.class));
            initTime();
            initView();
            initDanmu();
            initBattery();
            playMedia();
        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
        }
    }

    @Override
    protected void loadData(Bundle savedInstanceState) {
        try {
            new Thread(this).start();
        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
        }
    }

    private void initTime() {
        try {
            mTime = (TextView) findViewById(R.id.text_current_time);
        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
        }
    }

    private void initView() {
        try {
            mImageView = (ImageView) findViewById(R.id.image_view);
            mPlayerManager = new PlayerManager(this);
            mPlayerManager.setFullScreenOnly(true);
            mPlayerManager.setScaleType(PlayerManager.SCALETYPE_FITXY);
            mPlayerManager.playInFullScreen(true);
            mPlayerManager.setPlayerStateListener(this);
            boolean _support = mPlayerManager.isPlayerSupport();
            if (!_support) {
                ToastUtils.showToast(this, "播放器不支持此设备", Toast.LENGTH_LONG);
            }
        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
        }
    }

    /**
     * 播放视屏或者图片
     */
    private void playMedia() {
        try {
            if (mIndex >= mNowPlayInfoList.size() || mNowPlayInfoList.size() <= 0) {
                GlideUtils.loadImageView(this, R.mipmap.screen, mImageView);
                mImageView.setVisibility(View.VISIBLE);
                mPlayerManager.setVisibility(View.GONE);
                return;
            }
            String _mediaType = mNowPlayInfoList.get(mIndex).getFileType();
            String _filePath = getMediaPath(mNowPlayInfoList.get(mIndex).getFileName());
            if (android.text.TextUtils.isEmpty(_filePath)) {
                mIndex += 1;
                mImageHandler.sendEmptyMessage(ImageHandler.MSG_SEND);
                return;
            }
            switch (_mediaType) {
                case Constant.VIDEO:
                    mPlayerManager.play(_filePath);
                    mPlayerManager.setVisibility(View.VISIBLE);
                    mImageView.setVisibility(View.GONE);
                    break;
                case Constant.IMAGE:
                    GlideUtils.loadImamgeViewWithNoCache(this, _filePath, mImageView);
                    mImageView.setVisibility(View.VISIBLE);
                    mPlayerManager.setVisibility(View.GONE);
                    int _playLength = Integer.parseInt(mNowPlayInfoList.get(mIndex).getPlayLength());
                    mImageHandler.sendEmptyMessageDelayed(ImageHandler.MSG_SEND, 1000 * _playLength);
                    break;
            }
            mIndex += 1;
        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
        }
    }

    /**
     * 获取文件路径
     *
     * @param _fileName
     * @return
     */
    private String getMediaPath(String _fileName) {
        try {
            String _filePath = "";
            if (android.text.TextUtils.isEmpty(_fileName)) return _filePath;
            File _file = new File(mFile, _fileName);
            if (!_file.exists()) return _filePath;
            _filePath = _file.getPath();
            return _filePath;
        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
            return null;
        }
    }

    @Override
    public void onPrepared() {
        if (mScreenImage.getVisibility() == View.VISIBLE) {
            mPlayerManager.setVolumeSilence(0f, 0f);
        } else {
            mPlayerManager.setVolumeSilence(1f, 1f);
        }
    }

    @Override
    public void onComplete() {
        try {
            playMedia();
        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
        }
    }

    @Override
    public void onError() {
        try {
            ToastUtils.showToast(this, "onError", Toast.LENGTH_LONG);
        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
        }
    }

    @Override
    public void onLoading() {
        try {

        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
        }
    }

    @Override
    public void onPlay() {
        try {

        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
        }
    }

    public class ImageHandler extends Handler {

        //传递下一个播放消息
        public static final int MSG_SEND = 1;
        //时间显示
        public static final int MSG_TIME = 2;

        @Override
        public void handleMessage(Message msg) {
            try {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_SEND:
                        playMedia();
                        break;
                    case MSG_TIME:
                        mTime.setText(msg.obj.toString());
                        final long _currentTime = System.currentTimeMillis() / 1000;

                        //字幕
                        final Map<String, ?> _textStringMap = SPRollHelper.getInstance().getAll();
                        if (_textStringMap.containsKey(String.valueOf(_currentTime))) {
                            ExecutorManager.execute(new Runnable() {
                                @Override
                                public void run() {
                                    String _textJsonString = (String) _textStringMap.get(String.valueOf(_currentTime));
                                    if (!android.text.TextUtils.isEmpty(_textJsonString)) {
                                        final TextBean _textBean = FileAnalyzeUtil.getReadyText(_textJsonString);
                                        if (null != _textBean) {
                                            mTextBean = _textBean;
                                            mShowingTimes = _textBean.getRollTimes();//字幕播放次数
                                            MainActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    addDanmakuText(_textBean);
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                        }

                        //播放器
                        final Map<String, ?> _stringMap = SPPlayerHelper.getInstance().getAll();
                        if (_stringMap.containsKey(String.valueOf(_currentTime))) {
                            ExecutorManager.execute(new Runnable() {
                                @Override
                                public void run() {
                                    String _playJsonString = (String) _stringMap.get(String.valueOf(_currentTime));
                                    if (!android.text.TextUtils.isEmpty(_playJsonString)) {
                                        PlayerBean _playerBean = FileAnalyzeUtil.getReadyPlayerFileList(_playJsonString);
                                        if (null != _playerBean) {
                                            mNowPlayInfoList.clear();
                                            mNowPlayInfoList = _playerBean.getList();
                                            mPlayerTime = _playerBean.getPlayTimeInfo();
                                            mIndex = 0;
                                            mImageHandler.sendEmptyMessage(ImageHandler.MSG_SEND);
                                        }
                                    }
                                }
                            });
                        }
                        if (null != mPlayerTime) {
                            String _playEndTime = mPlayerTime.getEndTime();
                            long _endPlayerTime = DateUtils.formatToLongTime(DateUtil.timeFormat(_playEndTime)) / 1000;
                            if (_currentTime == _endPlayerTime) {
                                ExecutorManager.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        mNowPlayInfoList.clear();
                                        mImageHandler.sendEmptyMessage(ImageHandler.MSG_SEND);
                                    }
                                });
                            }
                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception _e) {
                LogUtils.catchInfo(_e.toString());
            }
        }

    }

    @Override
    public void run() {
        try {
            while (true) {
                //时间读取进程
                SimpleDateFormat _simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                String _str = _simpleDateFormat.format(new Date());
                Message _message = new Message();
                _message.obj = _str;
                _message.what = ImageHandler.MSG_TIME;
                mImageHandler.sendMessage(_message);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
        }
    }

    private void initDanmu() {
        try {
            mIDanmakuView = (IDanmakuView) findViewById(R.id.video_danmu);
            mContext = DanmakuContext.create();
            maxLinesPair = new HashMap<>();
            overlappingEnablePair = new HashMap<>();
            maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 3); // 滚动弹幕最大显示3行
            // 设置是否禁止重叠
            overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
            overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
            mContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_NONE) //设置描边样式
                    .setDuplicateMergingEnabled(false)//是否启用合并重复弹幕
                    .setMaximumLines(maxLinesPair) //设置最大显示行数
                    .preventOverlapping(overlappingEnablePair)//设置防弹幕重叠，null为允许重叠
            ;
            if (mIDanmakuView != null) {
                mBaseDanmakuParser = new BaseDanmakuParser() {
                    @Override
                    protected IDanmakus parse() {
                        return new Danmakus();
                    }
                };
                mIDanmakuView.setCallback(new master.flame.danmaku.controller.DrawHandler.Callback() {
                    @Override
                    public void updateTimer(DanmakuTimer timer) {
                        //子线程
                    }

                    @Override
                    public void drawingFinished() {
                        //主线程
                        if (mShowingTimes > 1) {
                            addDanmakuText(mTextBean);
                            mShowingTimes--;
                        }
                    }

                    @Override
                    public void danmakuShown(BaseDanmaku danmaku) {
                        //主线程
                    }

                    @Override
                    public void prepared() {
                        //子线程
                        mIDanmakuView.start();
                    }
                });

                mIDanmakuView.setOnDanmakuClickListener(new IDanmakuView.OnDanmakuClickListener() {

                    @Override
                    public boolean onDanmakuClick(IDanmakus danmakus) {
                        return false;
                    }

                    @Override
                    public boolean onDanmakuLongClick(IDanmakus danmakus) {
                        return false;
                    }

                    @Override
                    public boolean onViewClick(IDanmakuView view) {
                        return false;
                    }
                });
                mIDanmakuView.prepare(mBaseDanmakuParser, mContext);
                mIDanmakuView.showFPS(false); //是否显示FPS
                mIDanmakuView.enableDanmakuDrawingCache(true);
            }
        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
        }
    }

    /**
     * 播放弹幕
     *
     * @param _textBean
     */
    private void addDanmakuText(TextBean _textBean) {
        try {
            //默认弹幕值
            boolean _isBottom = false;
            int _color = getResources().getColor(R.color.color_ffffff);
            int _size = 20;
            float _speed = 1.0f;
            String _rollType = _textBean.getPosition();//位置
            switch (_rollType) {
                case Constant.TEXT_POSITION_MIDDLE:
                    _isBottom = false;
                    break;
                case Constant.TEXT_POSITION_LOWER:
                    _isBottom = true;
                    break;
                default:
                    break;
            }
            String _rollColor = _textBean.getFontColor();//颜色
            switch (_rollColor) {
                case Constant.TEXT_COLOR_WHITE:
                    _color = getResources().getColor(R.color.color_ffffff);
                    break;
                case Constant.TEXT_COLOR_READ:
                    _color = getResources().getColor(R.color.color_cf2e25);
                    break;
                default:
                    break;
            }
            String _rollSize = _textBean.getFontSize();//大小
            switch (_rollSize) {
                case Constant.TEXT_SIZE_BIG:
                    _size = Constant.TEXT_SIZE_BIG_B;
                    break;
                case Constant.TEXT_SIZE_SMALL:
                    _size = Constant.TEXT_SIZE_SMALL_S;
                    break;
                default:
                    break;
            }
            String _stringText = _textBean.getContent();//内容
            if (!android.text.TextUtils.isEmpty(_stringText) && _stringText.length() > 8) {
                _speed = _stringText.length() / 8;
            }
            addDanmaku(_isBottom, BaseDanmaku.TYPE_SCROLL_RL, _color, _size, _stringText, _speed);
        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
        }
    }

    /**
     * @param _bottom
     * @param _type
     * @param _color
     * @param _size
     * @param _content
     * @param _speed
     */
    private void addDanmaku(boolean _bottom, int _type, int _color, float _size, String _content, float _speed) {
        try {
            BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(_type);
            danmaku.textColor = _color;
            danmaku.textSize = TextUtils.sp2px(this, _size);
            danmaku.text = _content;
            danmaku.padding = 0;
            danmaku.priority = 1;  // 可能会被各种过滤器过滤并隐藏显示
            danmaku.isLive = false;
            danmaku.setTime(mIDanmakuView.getCurrentTime());
            mIDanmakuView.getConfig().setScrollSpeedFactor(_speed); //设置弹幕滚动速度系数,只对滚动弹幕有效，1f对应4s左右
            mIDanmakuView.getConfig().alignBottom(_bottom);
            mIDanmakuView.addDanmaku(danmaku);
        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
        }
    }

    private void initBattery() {
        try {
            mScreenImage = (ImageView) findViewById(R.id.image_screen);
            GlideUtils.loadImageView(MainActivity.this, R.mipmap.screen, mScreenImage);
            mBatteryListener = new BatteryListener(this);
            mBatteryListener.register(new BatteryStateListener() {
                @Override
                public void onStateChanged() {

                }

                @Override
                public void onStateLow() {

                }

                @Override
                public void onStateOkay() {

                }

                @Override
                public void onStatePowerConnected() {
                    mScreenImage.setVisibility(View.GONE);
                    mPlayerManager.setVolumeSilence(1f, 1f);
                }

                @Override
                public void onStatePowerDisconnected() {
                    mScreenImage.setVisibility(View.VISIBLE);
                    mPlayerManager.setVolumeSilence(0f, 0f);
                }
            });
        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
        }
    }

    @Override
    protected void onPause() {
        try {
            super.onPause();
            if (null != mPlayerManager) {
                mPlayerManager.onPause();
            }
            if (mIDanmakuView != null && mIDanmakuView.isPrepared()) {
                mIDanmakuView.pause();
            }
        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
        }
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
            ScreenUtil.hideBottomUIMenu(MainActivity.this);
            if (null != mPlayerManager) {
                mPlayerManager.onResume();
            }
            if (mIDanmakuView != null && mIDanmakuView.isPrepared() && mIDanmakuView.isPaused()) {
                mIDanmakuView.resume();
            }
        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
        }
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            if (mBatteryListener != null) {
                mBatteryListener.unregister();
            }
            if (null != mPlayerManager) {
                mPlayerManager.onDestroy();
            }
            if (mIDanmakuView != null) {
                // dont forget release!
                mIDanmakuView.release();
                mIDanmakuView = null;
            }
        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
        }
    }

    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
            if (mIDanmakuView != null) {
                // dont forget release!
                mIDanmakuView.release();
                mIDanmakuView = null;
            }
        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
        }
    }

    private static Boolean isESC = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                exitBy2Click();
                return true;
            }
        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 双击退出函数
     */
    private void exitBy2Click() {
        try {
            Timer tExit;
            if (!isESC) {
                isESC = true; // 准备退出
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                tExit = new Timer();
                tExit.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        isESC = false; // 取消退出
                    }
                }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
            } else {
                finish();
                System.exit(0);
            }
        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
        }
    }

}
