package com.millet.androidlib.UI.Adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewPager适配器
 * Created by Administrator on 2017/5/15 0015.
 */

public class CommonPagerAdapter extends PagerAdapter {

    private List<View> mListView = new ArrayList<>();

    public void setData(List<View> _list) {
        this.mListView.clear();
        if (null == _list || 0 == _list.size())
            return;
        this.mListView.addAll(_list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mListView.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * 添加页卡
     *
     * @param container
     * @param position
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mListView.get(position));
        return mListView.get(position);
    }

    /**
     * 删除页卡
     *
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mListView.get(position));
    }

}
