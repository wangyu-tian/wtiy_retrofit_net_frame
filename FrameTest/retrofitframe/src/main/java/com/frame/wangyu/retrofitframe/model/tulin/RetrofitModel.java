package com.frame.wangyu.retrofitframe.model.tulin;

import com.frame.wangyu.retrofitframe.RetrofitSingle;
import com.frame.wangyu.retrofitframe.api.TuLingApi;

import java.util.HashMap;

import com.frame.wangyu.retrofitframe.model.tulin.response.TuLingResponse;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by wangyu on 2019/4/24.
 * Retrofit + RxJAVA接口访问
 */

public class RetrofitModel {

    private RetrofitModel() {
    }
    private static class SingletonInstance {
        private static final RetrofitModel INSTANCE = new RetrofitModel();
    }

    public static RetrofitModel getInstance() {
        return SingletonInstance.INSTANCE;
    }

    /**
     * 请求图灵接口
     * @param text
     * @param subscriber
     */
    public void aiTuLing(String text, Subscriber<TuLingResponse> subscriber) {
        HashMap<String, String> headers = new HashMap<>(2);
//        headers.put("Content-Type", "application/x-www-form-urlencoded");
//        headers.put("Authorization", "Basic " + Base64.encode("yiyuan:yiyuanoa".getBytes()));
        HashMap<String, String> params = new HashMap<>(4);
        params.put("info", text);
        params.put("key","a9ba66f5f7ed40f5a1ab7c80fe3d6bea");
        Observable<TuLingResponse> observable = RetrofitSingle.getInstance().getRetrofitApi(TuLingApi.class).getMessageByTuLing(headers, params);
        RetrofitSingle.getInstance().toSubscribe(observable, subscriber);
    }
}
