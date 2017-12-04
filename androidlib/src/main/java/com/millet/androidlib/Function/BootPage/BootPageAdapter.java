package com.millet.androidlib.Function.BootPage;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 启动页的适配器
 * Created by Administrator on 2017/5/12 0012.
 */

public class BootPageAdapter extends PagerAdapter {
    private List<View> mView;

    public BootPageAdapter(List<View> _view) {
        mView = _view;
    }

    @Override
    public int getCount() {
        if (null == mView)
            return 0;
        return mView.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mView.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mView.get(position));
        return mView.get(position);
    }
}
