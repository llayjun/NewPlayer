package com.millet.androidlib.Utils;

import android.net.Uri;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/1/12.
 */

public class StringUtils {

    /**
     * 拼接get请求的url和param
     *
     * @param _url
     * @param _param
     * @return
     */
    public static String appendParams(String _url, Map<String, String> _param) {
        if (null == _url || null == _param || _param.isEmpty()) {
            return _url;
        }
        Uri.Builder _builder = Uri.parse(_url).buildUpon();
        Set<String> _keys = _param.keySet();
        Iterator<String> _iterator = _keys.iterator();
        while (_iterator.hasNext()) {
            String _key = _iterator.next();
            _builder.appendQueryParameter(_key, _param.get(_key));
        }
        return _builder.build().toString();
    }

}
