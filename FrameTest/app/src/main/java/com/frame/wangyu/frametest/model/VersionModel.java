package com.frame.wangyu.frametest.model;

import com.frame.wangyu.frametest.model.response.VersionResponse;
import com.frame.wangyu.retrofitframe.RetrofitSingle;
import com.frame.wangyu.retrofitframe.api.TuLingApi;
import com.frame.wangyu.retrofitframe.model.tulin.response.TuLingResponse;

import java.util.HashMap;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by wangyu on 2019/4/24.
 * Retrofit + RxJAVA接口访问
 */

public class VersionModel {

    private VersionModel() {
    }
    private static class SingletonInstance {
        private static final VersionModel INSTANCE = new VersionModel();
    }

    public static VersionModel getInstance() {
        return SingletonInstance.INSTANCE;
    }

    public void queryVersion(Subscriber<VersionResponse> subscriber) {
        Observable<VersionResponse> observable = RetrofitSingle.getInstance().getRetrofitApi(TestApi.class).getVersion();
        RetrofitSingle.getInstance().toSubscribe(observable, subscriber);
    }
}
