package com.millet.androidlib.UI.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.millet.androidlib.R;


/**
 * 圆环型的加载进度显示条
 * Created by llay on 2017/2/15.
 */

public class CircleProgressView extends View {
    private int mFirstColor, mSecondColor;
    private int mCircleWidth;
    private int mCurrentSpeed;
    private Paint mPaint;
    private int mCurrentProgress;
    private boolean isNext;

    public CircleProgressView(Context context) {
        this(context, null);
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (true) {
                    mCurrentProgress++;
                    if (360 == mCurrentProgress) {
                        mCurrentProgress = 0;
                        if (!isNext)
                            isNext = true;
                        else
                            isNext = false;
                    }
                    postInvalidate();
                    try {
                        Thread.sleep(mCurrentSpeed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int _centre = getWidth() / 2;
        int _radius = _centre - mCircleWidth / 2;
        mPaint.setStrokeWidth(mCircleWidth);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        RectF _oval = new RectF(_centre - _radius, _centre - _radius, _centre + _radius, _centre + _radius);
        if (!isNext) {
            mPaint.setColor(mFirstColor);
            canvas.drawCircle(_centre, _centre, _radius, mPaint);
            mPaint.setColor(mSecondColor);
            canvas.drawArc(_oval, -90, mCurrentProgress, false, mPaint);
        } else {
            mPaint.setColor(mSecondColor);
            canvas.drawCircle(_centre, _centre, _radius, mPaint);
            mPaint.setColor(mFirstColor);
            canvas.drawArc(_oval, -90, mCurrentProgress, false, mPaint);
        }
    }

    private void initAttr(Context _context, AttributeSet _attrs) {
        TypedArray _typeArray = _context.obtainStyledAttributes(_attrs, R.styleable.CircleView);
        mFirstColor = _typeArray.getColor(R.styleable.CircleView_firstColor, Color.GREEN);
        mSecondColor = _typeArray.getColor(R.styleable.CircleView_secondColor, Color.RED);
        mCircleWidth = _typeArray.getInt(R.styleable.CircleView_circleWidth, 20);
        mCurrentSpeed = _typeArray.getInt(R.styleable.CircleView_speed, 5);
        _typeArray.recycle();
    }

}
