package com.millet.androidlib.Cache;

import android.content.Context;
import android.text.TextUtils;

import com.millet.androidlib.Net.OkHttpManager;
import com.millet.androidlib.File.FileManager;
import com.millet.androidlib.Utils.NetWorkUtils;
import com.millet.androidlib.Utils.StringUtils;

import java.util.Map;

/**
 * Created by Administrator on 2017/1/12.
 * 用于缓存数据
 */

public class CacheAccessManager {

    /**
     * get同步请求，获取String类型，是否进行文件缓存
     *
     * @param _context 上下文
     * @param _url
     * @param _param
     * @param _cache   true代表缓存，false代表不缓存
     * @return
     */
    public static String getCacheStringSync(Context _context, String _url, Map<String, String> _param, boolean _cache) {
        String _lastUrl, _keyUrl, _result = null;
        if (null == _url) return null;
        _lastUrl = StringUtils.appendParams(_url, _param);
        if (null == _lastUrl) return null;
        _keyUrl = _lastUrl.replaceAll("/", "_");
        if (NetWorkUtils.isNetWorkAvailable(_context)) {
            _result = OkHttpManager.getStringSync(_lastUrl);
            if (_cache) FileManager.saveStringToCache(_context, _keyUrl, _result);
        }
        if (_cache && TextUtils.isEmpty(_result)) {
            _result = FileManager.getStringFromCache(_context, _keyUrl);
        }
        return _result;
    }

}
