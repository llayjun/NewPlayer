package com.millet.androidlib.UI.View;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import com.millet.androidlib.R;

/**
 * 底部弹出框，类似选择拍照选择相册取消的功能PopupWindow
 * Created by Administrator on 2017/4/24 0024.
 */

public class BottomPopup extends PopupWindow implements View.OnClickListener, PopupWindow.OnDismissListener {

    private OnPopupListener mListener;
    private Activity mActivity;

    public BottomPopup(Activity activity, OnPopupListener _listener) {
        super(activity);
        mActivity = activity;
        mListener = _listener;
        initView(activity);
    }

    private void initView(Context _context) {
        View _view = LayoutInflater.from(_context).inflate(R.layout.view_bottom_popup, null);
        this.setContentView(_view);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        this.setAnimationStyle(R.style.bottom_popup_animation_style);
        this.setOnDismissListener(this);
        Button _btnCamera = (Button) _view.findViewById(R.id.view_common_popup_camera);
        _btnCamera.setOnClickListener(this);
        Button _btnPhoto = (Button) _view.findViewById(R.id.view_common_popup_photo);
        _btnPhoto.setOnClickListener(this);
        Button _btnCancel = (Button) _view.findViewById(R.id.view_common_popup_cancel);
        _btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onDismiss();
        int _id = v.getId();
        if (R.id.view_common_popup_camera == _id) {
            mListener.onChoseCamera();
        } else if (R.id.view_common_popup_photo == _id) {
            mListener.onChosePhoto();
        } else if (R.id.view_common_popup_cancel == _id) {
            mListener.onChoseCancel();
        }
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        setBackgroundAlpha(0.8f);
        super.showAtLocation(parent, gravity, x, y);
    }

    @Override
    public void onDismiss() {
        this.dismiss();
        setBackgroundAlpha(1f);
    }

    private void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        mActivity.getWindow().setAttributes(lp);
    }

    public interface OnPopupListener {
        void onChoseCamera();//拍照

        void onChosePhoto();//选择相册

        void onChoseCancel();//取消
    }

}
