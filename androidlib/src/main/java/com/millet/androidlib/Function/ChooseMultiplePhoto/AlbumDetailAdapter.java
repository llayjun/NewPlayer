package com.millet.androidlib.Function.ChooseMultiplePhoto;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.millet.androidlib.Base.IBaseAdapter;
import com.millet.androidlib.R;

import java.io.File;

/**
 * Created by Administrator on 2017/5/18 0018.
 */

public class AlbumDetailAdapter extends IBaseAdapter {

    private SparseBooleanArray mSparseBooleanArray = new SparseBooleanArray();

    public AlbumDetailAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_album_detail_adapter;
    }

    @Override
    protected IBaseViewHolder getViewHolder(View _view) {
        ViewHolder _viewHolder = new ViewHolder();
        _viewHolder.mSimpleView = (SimpleDraweeView) _view.findViewById(R.id.album_detail_image);
        _viewHolder.mSelectedView = (ImageView) _view.findViewById(R.id.album_detail_selected);
        return _viewHolder;
    }

    @Override
    protected void updateView(IBaseViewHolder _holder, int _position) {
        String _path = (String) getItem(_position);
        ViewHolder _viewHolder = (ViewHolder) _holder;
        if (TextUtils.isEmpty(_path)) return;
        _viewHolder.mSimpleView.setImageURI(Uri.fromFile(new File(_path)));
        boolean _isSelected = mSparseBooleanArray.get(_position);
        if (_isSelected) {
            _viewHolder.mSelectedView.setVisibility(View.VISIBLE);
        } else {
            _viewHolder.mSelectedView.setVisibility(View.GONE);
        }
    }

    public class ViewHolder implements IBaseViewHolder {
        SimpleDraweeView mSimpleView;
        ImageView mSelectedView;
    }

    public void setSparseBooleanArray(SparseBooleanArray _sparseBooleanArray) {
        mSparseBooleanArray = _sparseBooleanArray;
    }

}
