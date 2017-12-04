package com.millet.androidlib.Net;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.millet.androidlib.Common.Constant;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/1/12.
 * OkHttp网络请求封装
 */

public class OkHttpManager {
    //静态实例
    private static OkHttpManager mOkHttpManager;
    //OkHttp实例
    private OkHttpClient mOkHttpClient;
    private Gson mGson;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * 构造函数
     */
    private OkHttpManager() {
        mOkHttpClient = new OkHttpClient();
        //设置链接超时，读取超时，写入超时
        mOkHttpClient.newBuilder().connectTimeout(10, TimeUnit.SECONDS);
        mOkHttpClient.newBuilder().readTimeout(10, TimeUnit.SECONDS);
        mOkHttpClient.newBuilder().writeTimeout(10, TimeUnit.SECONDS);
        mGson = new Gson();
    }

    /**
     * 单利模式，返回OkHttpManager实例
     *
     * @return
     */
    public static OkHttpManager getInstance() {
        if (null == mOkHttpManager) {
            mOkHttpManager = new OkHttpManager();
        }
        return mOkHttpManager;
    }

    /**
     * get 同步 String
     *
     * @param _url
     * @return String
     */
    public static String getStringSync(String _url) {
        Response _response;
        String _result = null;
        try {
            if (null == _url) return null;
            _response = getResponseSync(_url);
            if (null == _response) return null;
            _result = _response.body().string();
            if (null == _result) return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return _result;
    }

    /**
     * get 同步
     *
     * @param _url
     * @return response
     */
    public static Response getResponseSync(String _url) {
        return getInstance().getSync(_url);
    }

    /**
     * get 异步
     *
     * @param _url
     * @param _callBack
     */
    public void getResponseAsync(String _url, OkHttpCallBack _callBack) {
        getInstance().getAsync(_url, _callBack);
    }

    /**
     * get 同步方式基类
     **/
    public Response getSync(String _url) {
        if (null == _url) return null;
        Response _response;
        Request _request = new Request.Builder().url(_url).build();
        try {
            Call _call = mOkHttpClient.newCall(_request);
            _response = _call.execute();
            printLog(_response);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return _response;
    }

    /**
     * get 异步方式基类
     **/
    public void getAsync(String _url, final OkHttpCallBack _callBack) {
        if (null == _url) return;
        Request _request = new Request.Builder().url(_url).build();
        Call _call = mOkHttpClient.newCall(_request);
        _call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                _callBack.requestFailure(call.request(), e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    _callBack.requestSuccess(call, response);
                    printLog(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * post的同步请求
     *
     * @param _url
     * @param _params
     * @return
     */
    public Response postResponseSync(String _url, Param... _params) {
        Request request = buildPostRequest(_url, _params);
        Response _response = null;
        try {
            Call _call = mOkHttpClient.newCall(request);
            _response = _call.execute();
            printLog(_response);
        } catch (IOException _e) {
            _e.printStackTrace();
            return null;
        }
        return _response;
    }

    /**
     * post的异步请求
     *
     * @param _url
     * @param _callBack
     * @param _params
     */
    public void postAsync(String _url, final OkHttpCallBack _callBack, Param... _params) {
        Request _request = buildPostRequest(_url, _params);
        Call _call = mOkHttpClient.newCall(_request);
        _call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                _callBack.requestFailure(call.request(), e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    _callBack.requestSuccess(call, response);
                    printLog(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * post请求
     *
     * @param _url
     * @param _header
     * @param _headerValue
     * @param _okHttpCallBack
     */
    public void postAsyncWithNoParam(String _url, String _header, String _headerValue, final OkHttpCallBack _okHttpCallBack) {
        Request _request = new Request.Builder().addHeader(_header, _headerValue).url(_url).build();
        Call _call = mOkHttpClient.newCall(_request);
        _call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                _okHttpCallBack.requestFailure(call.request(), e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    _okHttpCallBack.requestSuccess(call, response);
                    printLog(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * post中的json请求
     *
     * @param _url
     * @param _header
     * @param _headerValue
     * @param _okHttpCallBack
     * @param _serializable
     */
    public void postAsyncJson(Serializable _serializable, String _url, String _header, String _headerValue, final OkHttpCallBack _okHttpCallBack) {
        String _jsonString = null;
        _jsonString = mGson.toJson(_serializable);
        RequestBody _requestBody = RequestBody.create(JSON, _jsonString);
        Request _request = new Request.Builder().addHeader(_header, _headerValue).post(_requestBody).url(_url).build();
        Call _call = mOkHttpClient.newCall(_request);
        _call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                _okHttpCallBack.requestFailure(call.request(), e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    _okHttpCallBack.requestSuccess(call, response);
                    printLog(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 打印log
     *
     * @param _response
     */
    private void printLog(Response _response) {
        if (Constant.OkHttpManager.DEBUG) {
            Log.e(Constant.OkHttpManager.CONSTANT_TAG, "thread:" + Thread.currentThread());
            Log.e(Constant.OkHttpManager.CONSTANT_TAG, "method:" + _response.request().method());
            Log.e(Constant.OkHttpManager.CONSTANT_TAG, "url:" + _response.request().url());
            Log.e(Constant.OkHttpManager.CONSTANT_TAG, "code:" + _response.code());
            Log.e(Constant.OkHttpManager.CONSTANT_TAG, "protocol:" + _response.protocol());
            if (!TextUtils.isEmpty(_response.message()))
                Log.e(Constant.OkHttpManager.CONSTANT_TAG, "message:" + _response.message());
        }
    }

    private Request buildPostRequest(String url, Param[] params) {
        if (params == null) {
            params = new Param[0];
        }
        FormBody.Builder builder = new FormBody.Builder();
        for (Param param : params) {
            builder.add(param.key, param.value);
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }

    public static class Param {

        String key;
        String value;

        public Param() {
        }

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

}
