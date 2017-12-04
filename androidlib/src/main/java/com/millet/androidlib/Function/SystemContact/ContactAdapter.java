package com.millet.androidlib.Function.SystemContact;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;


import com.millet.androidlib.R;

import java.util.List;

/**
 * Created by llay on 2017/3/21.
 * 联系人列表适配器
 */

public class ContactAdapter extends ArrayAdapter<ContactEntity> {

    //从外面传进来的Layout
    private int mResourceId;

    //字母分组工具
    private SectionIndexer mIndexer;

    public ContactAdapter(Context context, int resource, List<ContactEntity> objects) {
        super(context, resource, objects);
        mResourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder _viewHolder;
        if (null == convertView) {
            _viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(mResourceId, null);
            _viewHolder.mSortKey = (TextView) convertView.findViewById(R.id.list_item_contact_sort_key);
            _viewHolder.mLinearSortKey = (LinearLayout) convertView.findViewById(R.id.list_item_contact_sort_key_layout);
            _viewHolder.mName = (TextView) convertView.findViewById(R.id.list_item_contact_name);
            _viewHolder.mImageHeader = (ImageView) convertView.findViewById(R.id.list_item_contact_header);
            convertView.setTag(_viewHolder);
        } else {
            _viewHolder = (ViewHolder) convertView.getTag();
        }
        showView(position, _viewHolder);
        return convertView;
    }

    private void showView(int _position, ViewHolder _viewHolder) {
        ContactEntity _contactEntity = getItem(_position);
        //设置名字
        _viewHolder.mName.setText(_contactEntity.getName());
        int _section = mIndexer.getSectionForPosition(_position);
        int _positionForSection = mIndexer.getPositionForSection(_section);
        if (_position == _positionForSection) {
            _viewHolder.mSortKey.setText(_contactEntity.getSortKey());
            _viewHolder.mLinearSortKey.setVisibility(View.VISIBLE);
        } else {
            _viewHolder.mLinearSortKey.setVisibility(View.GONE);
        }
    }

    private class ViewHolder {
        private TextView mSortKey;
        private LinearLayout mLinearSortKey;
        private TextView mName;
        private ImageView mImageHeader;
    }

    /**
     * 给当前适配器传入一个分组工具。
     *
     * @param _indexer
     */
    public void setIndexer(SectionIndexer _indexer) {
        mIndexer = _indexer;
    }
}
