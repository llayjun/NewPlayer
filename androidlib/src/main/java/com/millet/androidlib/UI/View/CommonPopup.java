package com.millet.androidlib.UI.View;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.millet.androidlib.R;

/**
 * 普通的PopupWindow控件，可以从右上角从小变大
 * Created by Administrator on 2017/4/27 0027.
 */

public class CommonPopup extends PopupWindow implements PopupWindow.OnDismissListener, View.OnClickListener {

    private Activity mActivity;

    private int[] mIconRes;
    private String[] mItemStrings;
    private View.OnClickListener[] mOnclickListeners;

    public CommonPopup(Activity activity, int[] mIconRes, String[] mItemStrings, View.OnClickListener[] mOnclickListeners) {
        super(activity);
        mActivity = activity;
        this.mIconRes = mIconRes;
        this.mItemStrings = mItemStrings;
        this.mOnclickListeners = mOnclickListeners;
        initView(activity);
    }

    private void initView(Context _activity) {
        View _view = LayoutInflater.from(_activity).inflate(R.layout.view_common_popup, null);
        this.setContentView(_view);
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        this.setAnimationStyle(R.style.common_popup_animation_style);
        this.setOnDismissListener(this);

        View item0_layout = _view.findViewById(R.id.item0_layout);
        item0_layout.setOnClickListener(this);
        View item1_layout = _view.findViewById(R.id.item1_layout);
        item1_layout.setOnClickListener(this);
        View item2_layout = _view.findViewById(R.id.item2_layout);
        item2_layout.setOnClickListener(this);
        View item3_layout = _view.findViewById(R.id.item3_layout);
        item3_layout.setOnClickListener(this);
        View item4_layout = _view.findViewById(R.id.item4_layout);
        item4_layout.setOnClickListener(this);
        View item5_layout = _view.findViewById(R.id.item5_layout);
        item5_layout.setOnClickListener(this);

        TextView tv0 = (TextView) _view.findViewById(R.id.tv_item0);
        TextView tv1 = (TextView) _view.findViewById(R.id.tv_item1);
        TextView tv2 = (TextView) _view.findViewById(R.id.tv_item2);
        TextView tv3 = (TextView) _view.findViewById(R.id.tv_item3);
        TextView tv4 = (TextView) _view.findViewById(R.id.tv_item4);
        TextView tv5 = (TextView) _view.findViewById(R.id.tv_item5);

        ImageView iv0 = (ImageView) _view.findViewById(R.id.iv_item0);
        ImageView iv1 = (ImageView) _view.findViewById(R.id.iv_item1);
        ImageView iv2 = (ImageView) _view.findViewById(R.id.iv_item2);
        ImageView iv3 = (ImageView) _view.findViewById(R.id.iv_item3);
        ImageView iv4 = (ImageView) _view.findViewById(R.id.iv_item4);
        ImageView iv5 = (ImageView) _view.findViewById(R.id.iv_item5);

        switch (mItemStrings.length) {
            case 1:
                tv0.setText(mItemStrings[0]);
                iv0.setImageResource(mIconRes[0]);
                item1_layout.setVisibility(View.GONE);
                item2_layout.setVisibility(View.GONE);
                item3_layout.setVisibility(View.GONE);
                item4_layout.setVisibility(View.GONE);
                item5_layout.setVisibility(View.GONE);
                break;
            case 2:
                tv0.setText(mItemStrings[0]);
                tv1.setText(mItemStrings[1]);
                iv0.setImageResource(mIconRes[0]);
                iv1.setImageResource(mIconRes[1]);
                item2_layout.setVisibility(View.GONE);
                item3_layout.setVisibility(View.GONE);
                item4_layout.setVisibility(View.GONE);
                item5_layout.setVisibility(View.GONE);
                break;
            case 3:
                tv0.setText(mItemStrings[0]);
                tv1.setText(mItemStrings[1]);
                tv2.setText(mItemStrings[2]);
                iv0.setImageResource(mIconRes[0]);
                iv1.setImageResource(mIconRes[1]);
                iv2.setImageResource(mIconRes[2]);
                item3_layout.setVisibility(View.GONE);
                item4_layout.setVisibility(View.GONE);
                item5_layout.setVisibility(View.GONE);
                break;
            case 4:
                tv0.setText(mItemStrings[0]);
                tv1.setText(mItemStrings[1]);
                tv2.setText(mItemStrings[2]);
                tv3.setText(mItemStrings[3]);
                iv0.setImageResource(mIconRes[0]);
                iv1.setImageResource(mIconRes[1]);
                iv2.setImageResource(mIconRes[2]);
                iv3.setImageResource(mIconRes[3]);
                item4_layout.setVisibility(View.GONE);
                item5_layout.setVisibility(View.GONE);
                break;
            case 5:
                tv0.setText(mItemStrings[0]);
                tv1.setText(mItemStrings[1]);
                tv2.setText(mItemStrings[2]);
                tv3.setText(mItemStrings[3]);
                tv4.setText(mItemStrings[4]);
                iv0.setImageResource(mIconRes[0]);
                iv1.setImageResource(mIconRes[1]);
                iv2.setImageResource(mIconRes[2]);
                iv3.setImageResource(mIconRes[3]);
                iv4.setImageResource(mIconRes[4]);
                item5_layout.setVisibility(View.GONE);
                break;
            case 6:
                tv0.setText(mItemStrings[0]);
                tv1.setText(mItemStrings[1]);
                tv2.setText(mItemStrings[2]);
                tv3.setText(mItemStrings[3]);
                tv4.setText(mItemStrings[4]);
                tv5.setText(mItemStrings[5]);
                iv0.setImageResource(mIconRes[0]);
                iv1.setImageResource(mIconRes[1]);
                iv2.setImageResource(mIconRes[2]);
                iv3.setImageResource(mIconRes[3]);
                iv4.setImageResource(mIconRes[4]);
                iv5.setImageResource(mIconRes[5]);
                break;
        }
    }

    @Override
    public void onDismiss() {
        this.dismiss();
        setBackgroundAlpha(1f);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        setBackgroundAlpha(0.9f);
        super.showAsDropDown(anchor, xoff, yoff);
    }

    @Override
    public void onClick(View v) {
        this.onDismiss();
        if (v.getId() == R.id.item0_layout) {
            mOnclickListeners[0].onClick(v);
        } else if (v.getId() == R.id.item1_layout) {
            mOnclickListeners[1].onClick(v);
        } else if (v.getId() == R.id.item2_layout) {
            mOnclickListeners[2].onClick(v);
        } else if (v.getId() == R.id.item3_layout) {
            mOnclickListeners[3].onClick(v);
        } else if (v.getId() == R.id.item4_layout) {
            mOnclickListeners[4].onClick(v);
        } else if (v.getId() == R.id.item5_layout) {
            mOnclickListeners[5].onClick(v);
        }
    }

    private void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        mActivity.getWindow().setAttributes(lp);
    }

}
