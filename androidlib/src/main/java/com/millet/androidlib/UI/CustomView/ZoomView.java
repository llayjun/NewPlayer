package com.millet.androidlib.UI.CustomView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import static android.R.attr.width;

/**
 * 对图片显示，可以缩放操作，设置bitmap
 * Created by llay on 2017/3/29.
 */

public class ZoomView extends View {
    //图片初始状态常量
    private static final int STATUS_INIT = 1;
    //图片放大状态常量
    private static final int STATUS_ZOOM_OUT = 2;
    //图片缩小状态常量
    private static final int STATUS_ZOOM_IN = 3;
    //图片拖动状态常量
    private static final int STATUS_MOVE = 4;
    //用于对图片进行移动和缩放变换的矩阵
    private Matrix mMatrix = new Matrix();
    //待展现的bitmap图片
    private Bitmap mSourceBitmap;
    //记录当前操作的状态，可选值为STATUS_INIT、STATUS_ZOOM_OUT、STATUS_ZOOM_IN和STATUS_MOVE
    private int mCurrentStatus;
    //ZoomImageView控件的宽度
    private int mWidth;
    //ZoomImageView控件的高度
    private int mHeight;
    //记录两指同时放在屏幕上时，中心点的横坐标值
    private float mCenterPointX;
    //记录两指同时放在屏幕上时，中心点的纵坐标值
    private float mCenterPointY;
    //记录当前图片的宽度，图片被缩放时，这个值会一起变动
    private float mCurrentBitmapWidth;
    //记录当前图片的高度，图片被缩放时，这个值会一起变动
    private float mCurrentBitmapHeight;
    //记录上次手指移动时的横坐标
    private float mLastXMove = -1;
    //记录上次手指移动时的纵坐标
    private float mLastYMove = -1;
    //记录手指在横坐标方向上的移动距离
    private float mMovedDistanceX;
    //记录手指在纵坐标方向上的移动距离
    private float mMovedDistanceY;
    //记录图片在矩阵上的横向偏移值
    private float mTotalTranslateX;
    //记录图片在矩阵上的纵向偏移值
    private float mTotalTranslateY;
    //记录图片在矩阵上的总缩放比例
    private float mTotalRatio;
    //记录手指移动的距离所造成的缩放比例
    private float mScaledRatio;
    //记录图片初始化时的缩放比例
    private float mInitRatio;
    //记录上次两指之间的距离
    private double mLastFingerDis;

    public ZoomView(Context context) {
        this(context, null);
    }

    public ZoomView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCurrentStatus = STATUS_INIT;
    }

    /**
     * 将待展示的图片设置进来。
     *
     * @param _bitmap 待展示的Bitmap对象
     */
    public void setImageBitmap(Bitmap _bitmap) {
        mSourceBitmap = _bitmap;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            //分别取到zoomView的宽高
            mWidth = getWidth();
            mHeight = getHeight();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 2) {
                    //当有两个手指按在屏幕上时，计算两指之间的距离
                    mLastFingerDis = distanceBetweenFingers(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 1) {
                    //只有一个手指为拖动状态
                    float _xMove = event.getX();
                    float _yMove = event.getY();
                    if (mLastXMove == -1 && mLastYMove == -1) {
                        mLastXMove = _xMove;
                        mLastYMove = _yMove;
                    }
                    mCurrentStatus = STATUS_MOVE;
                    mMovedDistanceX = _xMove - mLastXMove;
                    mMovedDistanceY = _yMove - mLastYMove;
                    //进行边界检查，不允许将图片拖出边界
                    if (mTotalTranslateX + mMovedDistanceX > 0) {
                        mMovedDistanceX = 0;
                    } else if (mWidth - (mTotalTranslateX + mMovedDistanceX) > mCurrentBitmapWidth) {
                        mMovedDistanceX = 0;
                    }
                    if (mTotalTranslateY + mMovedDistanceY > 0) {
                        mMovedDistanceY = 0;
                    } else if (mHeight - (mTotalTranslateY + mMovedDistanceY) > mCurrentBitmapHeight) {
                        mMovedDistanceY = 0;
                    }
                    // 调用onDraw()方法绘制图片
                    invalidate();
                    mLastXMove = _xMove;
                    mLastYMove = _yMove;
                } else if (event.getPointerCount() == 2) {
                    //有两个手指为缩放状态
                    centerPointBetweenFingers(event);
                    double _fingerDis = distanceBetweenFingers(event);
                    if (_fingerDis > mLastFingerDis) {
                        //放大
                        mCurrentStatus = STATUS_ZOOM_OUT;
                    } else {
                        //缩小
                        mCurrentStatus = STATUS_ZOOM_IN;
                    }
                    // 进行缩放倍数检查，最大只允许将图片放大4倍，最小可以缩小到初始化比例
                    if ((mCurrentStatus == STATUS_ZOOM_OUT && mTotalRatio < 4 * mInitRatio)
                            || (mCurrentStatus == STATUS_ZOOM_IN && mTotalRatio > mInitRatio)) {
                        mScaledRatio = (float) (_fingerDis / mLastFingerDis);
                        mTotalRatio = mTotalRatio * mScaledRatio;
                        if (mTotalRatio > 4 * mInitRatio) {
                            mTotalRatio = 4 * mInitRatio;
                        } else if (mTotalRatio < mInitRatio) {
                            mTotalRatio = mInitRatio;
                        }
                        // 调用onDraw()方法绘制图片
                        invalidate();
                        mLastFingerDis = _fingerDis;
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() == 2) {
                    // 手指离开屏幕时将临时值还原
                    mLastXMove = -1;
                    mLastYMove = -1;
                }
                break;
            case MotionEvent.ACTION_UP:
                // 手指离开屏幕时将临时值还原
                mLastXMove = -1;
                mLastYMove = -1;
                break;
        }
        return true;
    }

    /**
     * 根据currentStatus的值来决定对图片进行什么样的绘制操作。
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (mCurrentStatus) {
            case STATUS_ZOOM_OUT:
            case STATUS_ZOOM_IN:
                zoom(canvas);
                break;
            case STATUS_MOVE:
                move(canvas);
                break;
            case STATUS_INIT:
                initBitmap(canvas);
            default:
                canvas.drawBitmap(mSourceBitmap, mMatrix, null);
                break;
        }
    }

    /**
     * 对图片进行缩放处理。
     *
     * @param canvas
     */
    private void zoom(Canvas canvas) {
        mMatrix.reset();
        //将图片按总缩放比例进行缩放
        mMatrix.postScale(mTotalRatio, mTotalRatio);
        float scaledWidth = mSourceBitmap.getWidth() * mTotalRatio;
        float scaledHeight = mSourceBitmap.getHeight() * mTotalRatio;
        float translateX = 0f;
        float translateY = 0f;
        // 如果当前图片宽度小于屏幕宽度，则按屏幕中心的横坐标进行水平缩放。否则按两指的中心点的横坐标进行水平缩放
        if (mCurrentBitmapWidth < mWidth) {
            translateX = (mWidth - scaledWidth) / 2f;
        } else {
            translateX = mTotalTranslateX * mScaledRatio + mCenterPointX * (1 - mScaledRatio);
            // 进行边界检查，保证图片缩放后在水平方向上不会偏移出屏幕
            if (translateX > 0) {
                translateX = 0;
            } else if (mWidth - translateX > scaledWidth) {
                translateX = mWidth - scaledWidth;
            }
        }
        // 如果当前图片高度小于屏幕高度，则按屏幕中心的纵坐标进行垂直缩放。否则按两指的中心点的纵坐标进行垂直缩放
        if (mCurrentBitmapHeight < mHeight) {
            translateY = (mHeight - scaledHeight) / 2f;
        } else {
            translateY = mTotalTranslateY * mScaledRatio + mCenterPointY * (1 - mScaledRatio);
            // 进行边界检查，保证图片缩放后在垂直方向上不会偏移出屏幕
            if (translateY > 0) {
                translateY = 0;
            } else if (mHeight - translateY > scaledHeight) {
                translateY = mHeight - scaledHeight;
            }
        }
        // 缩放后对图片进行偏移，以保证缩放后中心点位置不变
        mMatrix.postTranslate(translateX, translateY);
        mTotalTranslateX = translateX;
        mTotalTranslateY = translateY;
        mCurrentBitmapWidth = scaledWidth;
        mCurrentBitmapHeight = scaledHeight;
        canvas.drawBitmap(mSourceBitmap, mMatrix, null);
    }

    /**
     * 对图片进行平移处理
     *
     * @param canvas
     */
    private void move(Canvas canvas) {
        mMatrix.reset();
        // 根据手指移动的距离计算出总偏移值
        float translateX = mTotalTranslateX + mMovedDistanceX;
        float translateY = mTotalTranslateY + mMovedDistanceY;
        // 先按照已有的缩放比例对图片进行缩放
        mMatrix.postScale(mTotalRatio, mTotalRatio);
        // 再根据移动距离进行偏移
        mMatrix.postTranslate(translateX, translateY);
        mTotalTranslateX = translateX;
        mTotalTranslateY = translateY;
        canvas.drawBitmap(mSourceBitmap, mMatrix, null);
    }

    /**
     * 对图片进行初始化操作，包括让图片居中，以及当图片大于屏幕宽高时对图片进行压缩。
     *
     * @param canvas
     */
    private void initBitmap(Canvas canvas) {
        if (mSourceBitmap != null) {
            mMatrix.reset();
            int bitmapWidth = mSourceBitmap.getWidth();
            int bitmapHeight = mSourceBitmap.getHeight();
            if (bitmapWidth > width || bitmapHeight > mHeight) {
                if (bitmapWidth - width > bitmapHeight - mHeight) {
                    // 当图片宽度大于屏幕宽度时，将图片等比例压缩，使它可以完全显示出来
                    float ratio = width / (bitmapWidth * 1.0f);
                    mMatrix.postScale(ratio, ratio);
                    float translateY = (mHeight - (bitmapHeight * ratio)) / 2f;
                    // 在纵坐标方向上进行偏移，以保证图片居中显示
                    mMatrix.postTranslate(0, translateY);
                    mTotalTranslateY = translateY;
                    mTotalRatio = mInitRatio = ratio;
                } else {
                    // 当图片高度大于屏幕高度时，将图片等比例压缩，使它可以完全显示出来
                    float ratio = mHeight / (bitmapHeight * 1.0f);
                    mMatrix.postScale(ratio, ratio);
                    float translateX = (width - (bitmapWidth * ratio)) / 2f;
                    // 在横坐标方向上进行偏移，以保证图片居中显示
                    mMatrix.postTranslate(translateX, 0);
                    mTotalTranslateX = translateX;
                    mTotalRatio = mInitRatio = ratio;
                }
                mCurrentBitmapWidth = bitmapWidth * mInitRatio;
                mCurrentBitmapHeight = bitmapHeight * mInitRatio;
            } else {
                // 当图片的宽高都小于屏幕宽高时，直接让图片居中显示
                float translateX = (mWidth - mSourceBitmap.getWidth()) / 2f;
                float translateY = (mHeight - mSourceBitmap.getHeight()) / 2f;
                mMatrix.postTranslate(translateX, translateY);
                mTotalTranslateX = translateX;
                mTotalTranslateY = translateY;
                mTotalRatio = mInitRatio = 1f;
                mCurrentBitmapWidth = bitmapWidth;
                mCurrentBitmapHeight = bitmapHeight;
            }
            canvas.drawBitmap(mSourceBitmap, mMatrix, null);
        }
    }

    /**
     * 计算两手指之间的距离
     *
     * @param _event
     * @return
     */

    private double distanceBetweenFingers(MotionEvent _event) {
        float _disx = Math.abs(_event.getX(0) - _event.getX(1));
        float _disy = Math.abs(_event.getY(0) - _event.getY(1));
        return Math.sqrt(_disx * _disx + _disy * _disy);
    }

    /**
     * 计算两个手指之间中心点的坐标。
     *
     * @param event
     */
    private void centerPointBetweenFingers(MotionEvent event) {
        float xPoint0 = event.getX(0);
        float yPoint0 = event.getY(0);
        float xPoint1 = event.getX(1);
        float yPoint1 = event.getY(1);
        mCenterPointX = (xPoint0 + xPoint1) / 2;
        mCenterPointY = (yPoint0 + yPoint1) / 2;
    }

}
