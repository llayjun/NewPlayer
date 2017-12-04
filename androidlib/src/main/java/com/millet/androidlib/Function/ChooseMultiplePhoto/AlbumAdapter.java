package com.millet.androidlib.Function.ChooseMultiplePhoto;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.millet.androidlib.Base.IBaseAdapter;
import com.millet.androidlib.Function.TakePhoto.AlbumEntity;
import com.millet.androidlib.R;

import java.io.File;


/**
 * Created by Administrator on 2017/5/16 0016.
 */


public class AlbumAdapter extends IBaseAdapter {

    public AlbumAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_album_adapter;
    }

    @Override
    protected IBaseViewHolder getViewHolder(View _view) {
        ViewHolder _viewHolder = new ViewHolder();
        _viewHolder.mImage = (SimpleDraweeView) _view.findViewById(R.id.album_image);
        _viewHolder.mText = (TextView) _view.findViewById(R.id.album_text);
        return _viewHolder;
    }

    @Override
    protected void updateView(IBaseViewHolder _holder, int _position) {
        ViewHolder _viewHolder = (ViewHolder) _holder;
        AlbumEntity _albumEntity = (AlbumEntity) getItem(_position);
        if (null == _albumEntity) return;
        String _stringUrl = _albumEntity.getCoverPath();
        if (!TextUtils.isEmpty(_stringUrl)) {
            Uri _pathUri = Uri.fromFile(new File(_stringUrl));
            _viewHolder.mImage.setImageURI(_pathUri);
        }
        if (!TextUtils.isEmpty(_albumEntity.getName())) {
            _viewHolder.mText.setText(_albumEntity.getName() + "(" + _albumEntity.getPicCount() + ")");
        }
    }

    private class ViewHolder implements IBaseViewHolder {
        SimpleDraweeView mImage;
        TextView mText;
    }

}
