package com.millet.androidlib.Net;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/1/20.
 */

public interface OkHttpCallBack {
    void requestFailure(Request _request, IOException _io);

    void requestSuccess(Call call, Response _response) throws Exception;
}
