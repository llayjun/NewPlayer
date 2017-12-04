package com.millet.androidlib.Function.TakePhoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * http://blog.csdn.net/lmj623565791/article/details/39761281
 *
 * @author zhy
 */
public class ClipImageLayout extends RelativeLayout {

    private ClipZoomImageView mZoomImageView;
    private ClipImageBorderView mClipImageView;

    /**
     * 这里测试，直接写死了大小，真正使用过程中，可以提取为自定义属性
     */
    private int mHorizontalPadding = 20;

    public ClipImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mZoomImageView = new ClipZoomImageView(context);
        mClipImageView = new ClipImageBorderView(context);

        android.view.ViewGroup.LayoutParams lp = new LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);

        /**
         * 这里测试，直接写死了图片，真正使用过程中，可以提取为自定义属性
         */
        // mZoomImageView.setImageDrawable(getResources()
        // .getDrawable(R.drawable.a));

        this.addView(mZoomImageView, lp);
        this.addView(mClipImageView, lp);

        // 计算padding的px
        calcuHorizontalPadding(mHorizontalPadding);
    }

    private void calcuHorizontalPadding(int mHorizontalPadding) {
        mHorizontalPadding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, mHorizontalPadding, getResources()
                        .getDisplayMetrics());
        mZoomImageView.setHorizontalPadding(mHorizontalPadding);
        mClipImageView.setHorizontalPadding(mHorizontalPadding);
    }

    public void setImageDrawable(int resId) {
        mZoomImageView.setImageDrawable(getResources().getDrawable(resId));
    }

    public void setImageBitmap(Bitmap bitmap) {
        mZoomImageView.setImageBitmap(bitmap);
    }

    public void clear() {
        mZoomImageView.setImageBitmap(null);
    }

    /**
     * 对外公布设置边距的方法,单位为dp
     *
     * @param mHorizontalPadding
     */
    public void setHorizontalPadding(int mHorizontalPadding) {
        this.mHorizontalPadding = mHorizontalPadding;
        calcuHorizontalPadding(mHorizontalPadding);
    }

    public void setHWScale(double scale) {
        mClipImageView.setHVScale(scale);
        mZoomImageView.setHVScale(scale);
    }

    /*
     * 对外公布设置ClipImageBorderView（白色边框）隐藏的方法
     * 
     * @param hide
     */
    public void setClipImageBorderViewHide(boolean hide) {
        if (hide) {
            mClipImageView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 裁切图片
     *
     * @return
     */
    public Bitmap clip() {
        return mZoomImageView.clip();
    }

    public String saveToLocal(String path) {
        Bitmap bm = clip();
        if (null == bm) {
            return null;
        }
        // String path = FILE_LOCAL + "mm.jpg";
        try {
            FileOutputStream fos = new FileOutputStream(path);
            bm.compress(CompressFormat.JPEG, 80, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return path;
    }
}
