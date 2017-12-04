package com.millet.androidlib.UI.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by wanglinqi on 2015/7/3.
 */
public class MaxGridView extends GridView {

    public MaxGridView(Context context) {
        this(context, null);
    }

    public MaxGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaxGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureSpec1 = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec1);
    }

}
