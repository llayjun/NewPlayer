package com.millet.androidlib.Base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * BaseAdapter适配器
 * Created by Administrator on 2017/5/16 0016.
 */

public abstract class IBaseAdapter extends BaseAdapter {

    private Context mContext;
    private List<Object> mList;

    public IBaseAdapter(Context mContext) {
        this.mContext = mContext;
        mList = new ArrayList<>();
    }

    /**
     * 设置数据
     *
     * @param _list
     */
    public void setData(List<? extends Object> _list) {
        if (null != _list) {
            mList.clear();
            mList.addAll(_list);
            notifyDataSetChanged();
        }
    }

    /**
     * 添加数据
     *
     * @param _list
     */
    public void addData(List<? extends Object> _list) {
        if (null != _list) {
            mList.addAll(_list);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        if (position > mList.size()) {
            return null;
        }
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        IBaseViewHolder _viewHolder = null;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(getItemLayoutId(), null);
            _viewHolder = getViewHolder(convertView);
            convertView.setTag(_viewHolder);
        } else {
            _viewHolder = (IBaseViewHolder) convertView.getTag();
        }
        updateView(_viewHolder, position);
        return convertView;
    }

    protected abstract int getItemLayoutId();

    protected abstract IBaseViewHolder getViewHolder(View _view);

    protected abstract void updateView(IBaseViewHolder _holder, int _position);

    public interface IBaseViewHolder {
    }

    public Context getContext() {
        return mContext;
    }

}
