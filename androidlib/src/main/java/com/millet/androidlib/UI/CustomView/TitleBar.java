package com.millet.androidlib.UI.CustomView;

/**
 * Created by Administrator on 2017/9/7 0007.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.millet.androidlib.R;
import com.millet.androidlib.UI.InterfaceUi.TitleBarClickListener;

/**
 * 自定义标题栏
 *
 * @author Hunter
 */
public class TitleBar extends LinearLayout implements View.OnClickListener {
    private LayoutInflater mInflater;
    private View mHeader;
    private ImageView mLeftImageView, mRightImageView;
    private LinearLayout mHeaderTitle;
    private TextView mTvTitle;

    private TitleBarClickListener mTitleBarClickListener;

    public TitleBar(Context context) {
        super(context);
        init(context);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mInflater = LayoutInflater.from(context);
        mHeader = mInflater.inflate(R.layout.common_header, null);
        addView(mHeader);
        initViews();
    }

    public void setTitleBarClickListener(TitleBarClickListener _titleBarClickListener) {
        mTitleBarClickListener = _titleBarClickListener;
    }

    /**
     * 初始化布局
     */
    private void initViews() {
        mLeftImageView = (ImageView) findViewByHeaderId(R.id.head_left_image);
        mLeftImageView.setOnClickListener(this);
        mRightImageView = (ImageView) findViewByHeaderId(R.id.head_right_image);
        mRightImageView.setOnClickListener(this);
        mHeaderTitle = (LinearLayout) findViewByHeaderId(R.id.header_title);
        mTvTitle = (TextView) findViewByHeaderId(R.id.header_htv_subtitle);
    }

    /**
     * 在TitleBar中查找指定控件
     */
    public View findViewByHeaderId(int id) {
        return mHeader.findViewById(id);
    }

    /**
     * 自定义左侧视图
     */
    public void setLeftView(int _leftImageId) {
        mLeftImageView.setImageResource(_leftImageId);
    }

    /**
     * 自定义右侧视图
     */
    public void setRightView(int _rightImageId) {
        mRightImageView.setImageResource(_rightImageId);
    }

    public View getTitleView() {
        return mTvTitle;
    }

    /**
     * 设置TitleBar的标题
     */
    public void setTitleBar(CharSequence title) {
        mTvTitle.setText(title);
    }

    public void setTitleBar(CharSequence title, int _leftImageId, int _rightImageId, TitleBarClickListener _onClickListener) {
        setTitleBar(title);
        if (0 != _leftImageId) {
            setLeftView(_leftImageId);
        }
        if (0 != _rightImageId) {
            setRightView(_rightImageId);
        }
        setTitleBarClickListener(_onClickListener);
    }

    @Override
    public void onClick(View _view) {
        int _id = _view.getId();
        if (_id == R.id.head_left_image) {
            mTitleBarClickListener.leftOnClick();
        } else if (_id == R.id.head_right_image) {
            mTitleBarClickListener.rightOnClick();
        }
    }

}