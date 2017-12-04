package com.millet.androidlib.UI.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.millet.androidlib.Base.IBaseAdapter;
import com.millet.androidlib.R;
import com.millet.androidlib.UI.CustomView.SquareImageView;
import com.millet.androidlib.Utils.GlideUtils;
import com.yanzhenjie.album.AlbumFile;

/**
 * Created by Administrator on 2017/9/15 0015.
 */

public class GridViewImageAdapter extends IBaseAdapter {

    public GridViewImageAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_grid_view_image;
    }

    @Override
    protected IBaseViewHolder getViewHolder(View _view) {
        ViewHolder _viewHolder = new ViewHolder();
        _viewHolder.mImageView = (SquareImageView) _view.findViewById(R.id.item_grid_view_image);
        return _viewHolder;
    }

    @Override
    protected void updateView(IBaseViewHolder _holder, int _position) {
        ViewHolder _viewHolder = (ViewHolder) _holder;
        AlbumFile _albumFile = (AlbumFile) getItem(_position);
        if (null != _albumFile) {
            String _filePath = _albumFile.getPath();
            if (!TextUtils.isEmpty(_filePath)) {
                GlideUtils.loadImageView(getContext(), _filePath, _viewHolder.mImageView);
            }
        }
    }

    class ViewHolder implements IBaseViewHolder {
        private SquareImageView mImageView;
    }

}
