package com.millet.androidlib.UI.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * 标签式的自定义view，可自动换行
 * Created by llay on 2017/2/19.
 */

public class FlowViewGroup extends ViewGroup {

    private List<List<View>> mAllViews = new ArrayList<>();
    private List<Integer> mLineHeight = new ArrayList<>();

    public FlowViewGroup(Context context) {
        this(context, null);
    }

    public FlowViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取父容器给它设置的测量模式和大小
        int _widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int _widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int _heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int _heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //如果是wrap_content情况下，记录宽和高
        int _width = 0, _height = 0;
        //每一行的宽度
        int _lineWidth = 0;
        //每一行的高度
        int _lineHeight = 0;
        //子的个数
        int _countChildren = getChildCount();
        for (int i = 0; i < _countChildren; i++) {
            View _childView = getChildAt(i);
            //测量每个child的宽和高
            measureChild(_childView, widthMeasureSpec, heightMeasureSpec);
            //得到Child的lp
            MarginLayoutParams _lp = (MarginLayoutParams) _childView.getLayoutParams();
            //当前子布局实际占用的宽度
            int _widthChild = _lp.leftMargin + _lp.rightMargin + _childView.getMeasuredWidth();
            //当前子布局实际占用的高度
            int _heightChild = _lp.topMargin + _lp.bottomMargin + _childView.getMeasuredHeight();
            //如果当前的宽度大于最大宽度，则另外再起一行
            if (_lineWidth + _widthChild > _widthSize) {
                //取最大的
                _width = Math.max(_lineWidth, _widthChild);
                //重新开启新行，重新计算
                _lineWidth = _widthChild;
                //叠加当前的高度
                _height += _lineHeight;
                //开启记录下一行的高度
                _lineHeight = _heightChild;
            } else {
                //否则累加lineWidth，取最大值lineHeight
                _lineWidth += _widthChild;
                _lineHeight = Math.max(_lineHeight, _heightChild);
            }
            //如果是最后一个，则将当前记录的最大宽度和lineWidth作比较
            if (i == _countChildren - 1) {
                _width = Math.max(_width, _lineWidth);
                _height += _lineHeight;
            }
            setMeasuredDimension((_widthMode == MeasureSpec.EXACTLY) ? _widthSize
                    : _width, (_heightMode == MeasureSpec.EXACTLY) ? _heightSize
                    : _height);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeight.clear();
        int width = getWidth();
        int lineWidth = 0;
        int lineHeight = 0;
        // 存储每一行所有的childView
        List<View> lineViews = new ArrayList<>();
        int cCount = getChildCount();
        // 遍历所有的孩子
        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            // 如果已经需要换行
            if (childWidth + lp.leftMargin + lp.rightMargin + lineWidth > width) {
                // 记录这一行所有的View以及最大高度
                mLineHeight.add(lineHeight);
                // 将当前行的childView保存，然后开启新的ArrayList保存下一行的childView
                mAllViews.add(lineViews);
                lineWidth = 0;// 重置行宽
                lineViews = new ArrayList<>();
            }
            // 如果不需要换行，则累加
            lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
            lineHeight = Math.max(lineHeight, childHeight + lp.topMargin + lp.bottomMargin);
            lineViews.add(child);
        }
        // 记录最后一行
        mLineHeight.add(lineHeight);
        mAllViews.add(lineViews);
        int left = 0;
        int top = 0;
        // 得到总行数
        int lineNums = mAllViews.size();
        for (int i = 0; i < lineNums; i++) {
            // 每一行的所有的views
            lineViews = mAllViews.get(i);
            // 当前行的最大高度
            lineHeight = mLineHeight.get(i);
            // 遍历当前行所有的View
            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                //计算childView的left,top,right,bottom
                int lc = left + lp.leftMargin;
                int tc = top + lp.topMargin;
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();
                child.layout(lc, tc, rc, bc);
                left += child.getMeasuredWidth() + lp.rightMargin + lp.leftMargin;
            }
            left = 0;
            top += lineHeight;
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

}
