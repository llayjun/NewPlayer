package com.millet.androidlib.Cache;

/**
 * Created by Administrator on 2017/1/20.
 */

public interface CacheAccessCallBack {
    void requestCacheFailure(String _string);

    void requestCacheSuccess(String _string);
}
