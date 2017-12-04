package com.millet.androidlib.UI.CustomView;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.millet.androidlib.R;
import com.millet.androidlib.Utils.TextUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Banner，轮播图
 * Created by Administrator on 2017/5/15 0015.
 */

public class BannerView extends LinearLayout implements ViewPager.OnPageChangeListener {

    private final Context mContext;
    //UI
    public ViewPager mViewPager;
    private LinearLayout mLinearPoint;
    private BannerViewAdapter mAdapter;

    //DATA
    private List mList = new ArrayList();
    private ImageHandler mImageHandler;

    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initData();
        initView();
    }

    private void initData() {
        mList.add(R.mipmap.banner1);
        mList.add(R.mipmap.banner2);
        mList.add(R.mipmap.banner3);
        mList.add(R.mipmap.banner4);
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.view_banner, this, true);
        mViewPager = (ViewPager) findViewById(R.id.view_banner_view_pager);
        mLinearPoint = (LinearLayout) findViewById(R.id.view_banner_view_linear_point);
        mImageHandler = new ImageHandler(this);
        mAdapter = new BannerViewAdapter(mContext);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);
        mAdapter.setData(mList);
        initPoint(mList.size());
        mImageHandler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE_AUTO, ImageHandler.MSG_UPDATE_TIME);
    }

    /**
     * 初始化小圆点
     *
     * @param _num
     */
    private void initPoint(int _num) {
        if (0 == _num) {
            mLinearPoint.setVisibility(GONE);
        } else {
            mLinearPoint.removeAllViews();
            for (int i = 0; i < _num; i++) {
                ImageView _imageView = new ImageView(mContext);
                LinearLayout.LayoutParams _params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                if (0 == i) {
                    _imageView.setBackgroundResource(R.mipmap.point_selected);
                } else {
                    _imageView.setBackgroundResource(R.mipmap.point_un_selected);
                    _params.setMargins(TextUtils.dp2px(mContext, 7), 0, 0, 0);
                }
                _imageView.setLayoutParams(_params);
                mLinearPoint.addView(_imageView);
            }
            mLinearPoint.setVisibility(VISIBLE);
        }

    }

    /**
     * 设置小点
     *
     * @param _pointLinear
     * @param _selected
     */
    public void setPointToPosition(LinearLayout _pointLinear, int _selected) {
        _selected = _selected % mAdapter.getPageSize();
        if (null != _pointLinear) {
            int _pointCount = _pointLinear.getChildCount();
            for (int i = 0; i < _pointCount; i++) {
                if (i == _selected) {
                    _pointLinear.getChildAt(i).setBackgroundResource(R.mipmap.point_selected);
                } else {
                    _pointLinear.getChildAt(i).setBackgroundResource(R.mipmap.point_un_selected);
                }
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mImageHandler.sendMessage(Message.obtain(mImageHandler, ImageHandler.MSG_PAGE_CHANGED, position, 0));
        setPointToPosition(mLinearPoint, position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_IDLE://滚动状态闲置
                mImageHandler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE_AUTO, ImageHandler.MSG_UPDATE_TIME);
                break;
            case ViewPager.SCROLL_STATE_DRAGGING://滚动状态滑动中
                mImageHandler.sendEmptyMessage(ImageHandler.MSG_UPDATE_IMAGE_HAND);
                break;
            default:
                break;
        }
    }

    /**
     * ImageView Handler
     */
    public class ImageHandler extends Handler {
        //自动轮播
        public static final int MSG_UPDATE_IMAGE_AUTO = 1;
        //手动滑动
        public static final int MSG_UPDATE_IMAGE_HAND = 2;
        /**
         * 记录最新的页号，当用户手动滑动时需要记录新页号，否则会使轮播的页面出错。
         * 例如当前如果在第一页，本来准备播放的是第二页，而这时候用户滑动到了末页，
         * 则应该播放的是第一页，如果继续按照原来的第二页播放，则逻辑上有问题。
         */
        public static final int MSG_PAGE_CHANGED = 3;
        //轮播间隔时间
        public static final int MSG_UPDATE_TIME = 3000;

        private WeakReference<View> mView;

        private int mCurrentItem = 0;

        public ImageHandler(View _view) {
            mView = new WeakReference<View>(_view);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            View _view = mView.get();
            BannerView _bannerView = (BannerView) _view;
            if (null == _bannerView)
                return;
            //检查消息队列并移除未发送的消息，这主要是避免在复杂环境下消息出现重复等问题。
            if (_bannerView.mImageHandler.hasMessages(MSG_UPDATE_IMAGE_AUTO)) {
                _bannerView.mImageHandler.removeMessages(MSG_UPDATE_IMAGE_AUTO);
            }
            switch (msg.what) {
                case MSG_UPDATE_IMAGE_AUTO://自动
                    if (1 == ((BannerViewAdapter) _bannerView.mViewPager.getAdapter()).getPageSize())
                        break;
                    mCurrentItem++;
                    _bannerView.mViewPager.setCurrentItem(mCurrentItem);
                    //准备下一次自动播放
                    mImageHandler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE_AUTO, ImageHandler.MSG_UPDATE_TIME);
                    break;
                case MSG_UPDATE_IMAGE_HAND://手动
                    //只要不发送消息就暂停了
                    break;
                case MSG_PAGE_CHANGED:
                    //记录当前的页号，避免播放的时候页面显示不正确。
                    mCurrentItem = msg.arg1;
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * banner适配器
     */
    public class BannerViewAdapter extends PagerAdapter {

        private List<Object> mList = new ArrayList<>();
        private int mPageSize;
        private Context mContext;

        public BannerViewAdapter(Context _context) {
            mContext = _context;
        }

        public void setData(List<Object> _list) {
            this.mList.clear();
            if (null == _list || 0 == _list.size())
                return;
            this.mList.addAll(_list);
            this.mPageSize = mList.size();
            notifyDataSetChanged();
        }

        public Object getItem(int _position) {
            if (null == mList) {
                return null;
            }
            return mList.get(_position);
        }

        public int getPageSize() {
            return mPageSize;
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        /**
         * 添加页卡
         *
         * @param container
         * @param position
         * @return
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View _view = LayoutInflater.from(mContext).inflate(R.layout.item_banner_page_adapter, null);
            ImageView _image = (ImageView) _view.findViewById(R.id.item_banner_page_image_view);
            if (position % mPageSize < mList.size()) {
                _image.setImageResource((Integer) getItem(position % mPageSize));
            } else {
                _image.setImageResource(R.mipmap.banner_default);
            }
            container.addView(_view);
            return _view;
        }

        /**
         * 删除页卡
         *
         * @param container
         * @param position
         * @param object
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (object instanceof View)
                container.removeView((View) object);
        }
    }

}
