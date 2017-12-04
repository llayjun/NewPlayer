package com.millet.androidlib.UI.CustomView;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * 自适应高度的ViewPager，一般都设置不可滑动，不然会卡
 * Created by Administrator on 2017/5/12 0012.
 */

public class AdaptHeightViewPager extends ViewPager {

    private int current;//当前页
    private int height = 0;//高度

    private boolean scrollable = false;//是否可滑动

    public AdaptHeightViewPager(Context context) {
        this(context, null);
    }

    public AdaptHeightViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                resetHeight(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        resetHeight(0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getChildCount() > current) {
            View _child = getChildAt(current);
            _child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int _h = getMeasuredHeight();
            height = _h;
        }
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 重置高度
     *
     * @param _current
     */
    public void resetHeight(int _current) {
        this.current = _current;
        if (getChildCount() > _current) {
            LinearLayout.LayoutParams _layoutParams = (LinearLayout.LayoutParams) getLayoutParams();
            if (null == _layoutParams) {
                _layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            } else {
                _layoutParams.height = height;
            }
            setLayoutParams(_layoutParams);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!scrollable) {
            return false;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!scrollable) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public boolean isScrollble() {
        return scrollable;
    }

    public void setScrollble(boolean scrollble) {
        this.scrollable = scrollble;
    }

}
