package nj.com.myplayer;

import android.content.res.Configuration;
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

import com.millet.androidlib.Base.BaseActivity;
import com.millet.androidlib.Utils.GlideUtils;
import com.millet.androidlib.Utils.TextUtils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;
import nj.com.myplayer.model.MediaBean;
import nj.com.myplayer.utils.FileUtil;

public class MainActivity extends BaseActivity implements MediaPlayer.OnCompletionListener {

    private String url1 = "http://112.253.22.157/17/z/z/y/u/zzyuasjwufnqerzvyxgkuigrkcatxr/hc.yinyuetai.com/D046015255134077DDB3ACA0D7E68D45.flv";
    private String url2 = "http://flashmedia.eastday.com/newdate/news/2016-11/shznews1125-19.mp4";
    private String url3 = Environment.getExternalStorageDirectory() + "/a.mp4";
    private ArrayList<MediaBean> mVideoList = new ArrayList<>();
    private File mFile = new File(Environment.getExternalStorageDirectory(), "millet");

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

    //data
    //设置弹幕的最大显示行数
    private HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
    //设置是否禁止重叠
    private HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
    private int mIndex = 0;//记录已经播放到第几个了
    private ImageHandler mImageHandler = new ImageHandler();

    @Override
    protected void initData(Bundle savedInstanceState) {
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //获得当前窗体对象
        Window window = this.getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);
        Vitamio.initialize(this);
        FileUtil.getVideoFile(mVideoList, mFile);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(this))
            return;
        initView();
        initDanmu();
    }

    @Override
    protected void loadData(Bundle savedInstanceState) {

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
        playMedia();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playMedia();
    }

    /**
     * 播放视屏或者图片
     */
    private void playMedia() {
        if (mIndex >= mVideoList.size()) {
            mIndex = 0;
        }
        int _mediaType = mVideoList.get(mIndex).getType();
        switch (_mediaType) {
            case MediaBean.MEDIA_TYPE_VIDEO:
                mVideoView.setVideoURI(Uri.parse(mVideoList.get(mIndex).getMediaPath()));
                mVideoView.setVisibility(View.VISIBLE);
                mImageView.setVisibility(View.GONE);
                break;
            case MediaBean.MEDIA_TYPE_IMAGE:
                GlideUtils.loadImageView(this, mVideoList.get(mIndex).getMediaPath(), mImageView);
                mImageView.setVisibility(View.VISIBLE);
                mVideoView.setVisibility(View.GONE);
                mImageHandler.sendEmptyMessageDelayed(ImageHandler.MSG_SEND, ImageHandler.IMAGE_SHOW_TIME);
                break;
        }
        mIndex += 1;
    }

    public class ImageHandler extends Handler {

        //图片显示几秒
        public static final long IMAGE_SHOW_TIME = 2000;
        //传递下一个播放消息
        public static final int MSG_SEND = 1;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //检查消息队列并移除未发送的消息，这主要是避免在复杂环境下消息出现重复等问题。
            if (this.hasMessages(MSG_SEND)) {
                this.removeMessages(MSG_SEND);
            }
            switch (msg.what) {
                case MSG_SEND:
                    playMedia();
                    break;
                default:
                    break;
            }
        }

    }

    private void initDanmu() {
        mIDanmakuView = (IDanmakuView) findViewById(R.id.video_danmu);
        mContext = DanmakuContext.create();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 3); // 滚动弹幕最大显示3行
        // 设置是否禁止重叠
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_LR, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_BOTTOM, true);
        mContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3) //设置描边样式
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

                }

                @Override
                public void drawingFinished() {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {

                }

                @Override
                public void prepared() {
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
                    addDanmaku("哈哈哈哈哈哈");
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
     * 创建解析器对象，解析输入流
     *
     * @param stream
     * @return
     */
    private BaseDanmakuParser createParser(InputStream stream) {
        if (stream == null) {
            return new BaseDanmakuParser() {

                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }
        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);
        try {
            loader.load(stream);
        } catch (IllegalDataException e) {
            e.printStackTrace();
        }
        BaseDanmakuParser parser = new BiliDanmukuParser();
        IDataSource<?> dataSource = loader.getDataSource();
        parser.load(dataSource);
        return parser;
    }

    private void addDanmaku(String _content) {
        BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        danmaku.text = _content;
        danmaku.padding = 5;
        danmaku.priority = 1;  // 可能会被各种过滤器过滤并隐藏显示
        danmaku.isLive = false;
        danmaku.textSize = TextUtils.sp2px(this, 20);
        danmaku.textColor = Color.WHITE;
        danmaku.setTime(mIDanmakuView.getCurrentTime());
        mIDanmakuView.addDanmaku(danmaku);
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mIDanmakuView.getConfig().setDanmakuMargin(20);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mIDanmakuView.getConfig().setDanmakuMargin(40);
        }
    }

}
