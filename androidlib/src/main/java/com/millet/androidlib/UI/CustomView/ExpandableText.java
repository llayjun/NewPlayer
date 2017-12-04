package com.millet.androidlib.UI.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * 可以展开和收起的文字显示，只能有两个TextView的子类
 * Created by Administrator on 2017/5/19 0019.
 */

public class ExpandableText extends LinearLayout {
    private static final int MAX_LINE = 3; //未分开，最多显示3行

    private int mMaxLine = Integer.MAX_VALUE;

    private boolean mIsDefaultExpand = false;//默认已经打开
    private boolean mIsExpanded = false;//是否已经打开
    private boolean mIsMeasure = false;

    private TextView mExpandText;
    private TextView mClickText;

    private ExpandChangeListener mListener;

    public ExpandableText(Context context) {
        this(context, null);
    }

    public ExpandableText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mExpandText = (TextView) getChildAt(0);
        mClickText = (TextView) getChildAt(1);
        int _lineExpandText = mExpandText.getLineCount();
        if (!mIsMeasure) {
            if (_lineExpandText <= MAX_LINE) {
                mClickText.setVisibility(GONE);
            } else {
                if (mIsDefaultExpand) {
                    mExpandText.setMaxLines(mMaxLine);
                    mClickText.setVisibility(VISIBLE);
                    mClickText.setText("收起");
                    mClickText.setOnClickListener(mOnClick);
                    mIsExpanded = true;
                } else {
                    mExpandText.setMaxLines(MAX_LINE);
                    mClickText.setVisibility(VISIBLE);
                    mClickText.setText("展开");
                    mClickText.setOnClickListener(mOnClick);
                    mIsExpanded = false;
                }
            }
            setmIsMeasure(true);
        }
    }

    public OnClickListener mOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView _clickText = (TextView) v;
            if (mIsExpanded) {
                mExpandText.setMaxLines(MAX_LINE);
                _clickText.setText("展开");
                mIsExpanded = false;
                if (null != mListener)
                    mListener.onExpandChanged(false);
            } else {
                mExpandText.setMaxLines(mMaxLine);
                _clickText.setText("收起");
                mIsExpanded = true;
                if (null != mListener)
                    mListener.onExpandChanged(true);
            }
            mExpandText.postInvalidate();
        }
    };

    public boolean ismIsDefaultExpand() {
        return mIsDefaultExpand;
    }

    public void setmIsDefaultExpand(boolean mIsDefaultExpand) {
        this.mIsDefaultExpand = mIsDefaultExpand;
    }

    public boolean ismIsMeasure() {
        return mIsMeasure;
    }

    public void setmIsMeasure(boolean mIsMeasure) {
        this.mIsMeasure = mIsMeasure;
    }

    public interface ExpandChangeListener {
        void onExpandChanged(boolean _expand);
    }

    /**
     * 设置改变扩展的监听
     *
     * @param _listener
     */
    public void setExpandChangeListener(ExpandChangeListener _listener) {
        mListener = _listener;
    }
}
