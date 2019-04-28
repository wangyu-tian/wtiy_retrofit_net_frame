package com.frame.wangyu.retrofitframe.common;


import com.frame.wangyu.retrofitframe.util.LogUtils;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Copyright © 2018 Yiyuan Networks 上海义援网络科技有限公司. All rights reserved.
 *
 * @author Created by Wangpeng on 2018/5/23 11:26.
 */
public class LogInterceptor implements Interceptor {
    
    String TAG = "LogInterceptor";
    
    String content;
    
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        LogUtils.i(TAG, "request:" + request.toString());
        long t1 = System.nanoTime();
        Response response = chain.proceed(chain.request());
        long t2 = System.nanoTime();
        LogUtils.i(TAG, String.format(Locale.getDefault(), "Received response for %s in %.1fms%n%s", response.request
                ().url(), (t2 - t1) / 1e6d, response.headers()));
        okhttp3.MediaType mediaType = response.body().contentType();
        ResponseBody originalBody = response.body();
        
        if (null != originalBody) {
            content = originalBody.string();
        }
        LogUtils.i(TAG, "response body:" + content);
        return response.newBuilder().body(ResponseBody.create(mediaType, content)).build();
    }
}
