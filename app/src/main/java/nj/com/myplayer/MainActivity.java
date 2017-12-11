package nj.com.myplayer;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.millet.androidlib.Base.BaseActivity;
import com.millet.androidlib.Utils.DateUtils;
import com.millet.androidlib.Utils.GlideUtils;
import com.millet.androidlib.Utils.TextUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;
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
import nj.com.myplayer.utils.DateUtil;
import nj.com.myplayer.utils.SPPlayerHelper;
import nj.com.myplayer.utils.SPRollHelper;

import static nj.com.myplayer.R.mipmap.a;

public class MainActivity extends BaseActivity implements MediaPlayer.OnCompletionListener, Runnable {

    private String url1 = "http://112.253.22.157/17/z/z/y/u/zzyuasjwufnqerzvyxgkuigrkcatxr/hc.yinyuetai.com/D046015255134077DDB3ACA0D7E68D45.flv";
    private String url2 = "http://flashmedia.eastday.com/newdate/news/2016-11/shznews1125-19.mp4";
    private String url3 = Environment.getExternalStorageDirectory() + "/a.mp4";
    private File mFile = new File(Environment.getExternalStorageDirectory(), "millet");

    //广播监听电量状态
    private BatteryListener mBatteryListener;
    //UI
    //视屏
    private VideoView mVideoView;
    private MyMediaController mMediaController;
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
    private HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
    //设置是否禁止重叠
    private HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
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
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //获得当前窗体对象
        Window window = this.getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);
        Vitamio.initialize(this);
        FileAnalyzeUtil.savePlayInfo2Shared(this, mFile.getPath());
        FileAnalyzeUtil.saveRollTextInfo2Shared(this, mFile.getPath());
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(this))
            return;
        initTime();
        initView();
        initDanmu();
        initBattery();
        playMedia();
    }

    @Override
    protected void loadData(Bundle savedInstanceState) {
        new Thread(this).start();
    }

    private void initTime() {
        mTime = (TextView) findViewById(R.id.text_current_time);
    }

    private void initView() {
        mVideoView = (VideoView) findViewById(R.id.video_view);
        mImageView = (ImageView) findViewById(R.id.image_view);
        mMediaController = new MyMediaController(this, mVideoView, this);//实例化控制器
        mMediaController.show(5000);//控制器显示5s后自动隐藏
        mVideoView.setMediaController(mMediaController);//绑定控制器;
        mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);//设置播放画质 高画质
        mVideoView.requestFocus();//取得焦点
        mVideoView.setOnCompletionListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playMedia();
    }

    /**
     * 播放视屏或者图片
     */
    private void playMedia() {
        if (mIndex >= mNowPlayInfoList.size() || mNowPlayInfoList.size() <= 0) {
            GlideUtils.loadImageView(this, a, mImageView);
            mImageView.setVisibility(View.VISIBLE);
            mVideoView.setVisibility(View.GONE);
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
                mVideoView.setVideoURI(Uri.parse(_filePath));
                mVideoView.start();
                mVideoView.setVisibility(View.VISIBLE);
                mImageView.setVisibility(View.GONE);
                break;
            case Constant.IMAGE:
                GlideUtils.loadImageView(this, _filePath, mImageView);
                mImageView.setVisibility(View.VISIBLE);
                mVideoView.setVisibility(View.GONE);
                long _playLength = Long.parseLong(mNowPlayInfoList.get(mIndex).getPlayLength());
                mImageHandler.sendEmptyMessageDelayed(ImageHandler.MSG_SEND, _playLength);
                break;
        }
        mIndex += 1;
    }

    /**
     * 获取文件路径
     *
     * @param _fileName
     * @return
     */
    private String getMediaPath(String _fileName) {
        String _filePath = "";
        if (android.text.TextUtils.isEmpty(_fileName)) return _filePath;
        File _file = new File(mFile, _fileName);
        if (!_file.exists()) return _filePath;
        _filePath = _file.getPath();
        return _filePath;
    }

    public class ImageHandler extends Handler {

        //传递下一个播放消息
        public static final int MSG_SEND = 1;
        //时间显示
        public static final int MSG_TIME = 2;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SEND:
                    playMedia();
                    break;
                case MSG_TIME:
                    mTime.setText(msg.obj.toString());
                    long _currentTime = System.currentTimeMillis() / 1000;

                    //字幕
                    Map<String, ?> _textStringMap = SPRollHelper.getInstance().getAll();
                    if (_textStringMap.containsKey(String.valueOf(_currentTime))) {
                        String _textJsonString = (String) _textStringMap.get(String.valueOf(_currentTime));
                        if (!android.text.TextUtils.isEmpty(_textJsonString)) {
                            TextBean _textBean = FileAnalyzeUtil.getReadyText(_textJsonString);
                            if (null != _textBean) {
                                mTextBean = _textBean;
                                mShowingTimes = _textBean.getRollTimes();//字幕播放次数
                                addDanmakuText(_textBean);
                            }
                        }

                    }

                    //播放器
                    Map<String, ?> _stringMap = SPPlayerHelper.getInstance().getAll();
                    if (_stringMap.containsKey(String.valueOf(_currentTime))) {
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
                    if (null != mPlayerTime) {
                        String _playEndTime = mPlayerTime.getEndTime();
                        long _endPlayerTime = DateUtils.formatToLongTime(DateUtil.timeFormat(_playEndTime)) / 1000;
                        if (_currentTime == _endPlayerTime) {
                            mNowPlayInfoList.clear();
                            mImageHandler.sendEmptyMessage(ImageHandler.MSG_SEND);
                        }
                    }
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public void run() {
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
    }

    private void initDanmu() {
        mIDanmakuView = (IDanmakuView) findViewById(R.id.video_danmu);
        mContext = DanmakuContext.create();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 3); // 滚动弹幕最大显示3行
        // 设置是否禁止重叠
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
        mContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_NONE) //设置描边样式
                .setDuplicateMergingEnabled(false)//是否启用合并重复弹幕
                .setScrollSpeedFactor(1.2f) //设置弹幕滚动速度系数,只对滚动弹幕有效
                .setScaleTextSize(1.2f)
                .setMaximumLines(maxLinesPair) //设置最大显示行数
                .preventOverlapping(overlappingEnablePair)//设置防弹幕重叠，null为允许重叠
                .setDanmakuMargin(40);
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
                    if (mVideoView.getVisibility() == View.VISIBLE) {
                        mMediaController.show();
                    } else {
                        mMediaController.hide();
                    }
                    return false;
                }
            });
            mIDanmakuView.prepare(mBaseDanmakuParser, mContext);
            mIDanmakuView.showFPS(false); //是否显示FPS
            mIDanmakuView.enableDanmakuDrawingCache(true);
        }
    }

    /**
     * 播放弹幕
     *
     * @param _textBean
     */
    private void addDanmakuText(TextBean _textBean) {
        String _rollType = _textBean.getPosition();//位置
        String _rollColor = _textBean.getFontColor();//颜色
        String _rollSize = _textBean.getFontSize();//大小
        String _stringText = _textBean.getContent();//内容
        addDanmaku(BaseDanmaku.TYPE_SCROLL_RL, Color.WHITE, 20, _stringText);
    }

    /**
     * @param _type
     * @param _content
     * @param _size
     */
    private void addDanmaku(int _type, int _color, float _size, String _content) {
        BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(_type);
        danmaku.textColor = _color;
        danmaku.textSize = TextUtils.sp2px(this, _size);
        danmaku.text = _content;
        danmaku.padding = 100;
        danmaku.priority = 1;  // 可能会被各种过滤器过滤并隐藏显示
        danmaku.isLive = false;
        danmaku.setTime(mIDanmakuView.getCurrentTime());
        mIDanmakuView.addDanmaku(danmaku);
    }

    private void initBattery() {
        mScreenImage = (ImageView) findViewById(R.id.image_screen);
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
                mVideoView.setVolume(1f, 1f);
            }

            @Override
            public void onStatePowerDisconnected() {
                GlideUtils.loadImageView(MainActivity.this, a, mScreenImage);
                mScreenImage.setVisibility(View.VISIBLE);
                mVideoView.setVolume(0f, 0f);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIDanmakuView != null && mIDanmakuView.isPrepared()) {
            mIDanmakuView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIDanmakuView != null && mIDanmakuView.isPrepared() && mIDanmakuView.isPaused()) {
            mIDanmakuView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        if (mBatteryListener != null) {
            mBatteryListener.unregister();
        }
        super.onDestroy();
        if (mIDanmakuView != null) {
            // dont forget release!
            mIDanmakuView.release();
            mIDanmakuView = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mIDanmakuView != null) {
            // dont forget release!
            mIDanmakuView.release();
            mIDanmakuView = null;
        }
    }

}
