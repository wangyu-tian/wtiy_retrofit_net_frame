package com.frame.wangyu.frametest.model;

import com.frame.wangyu.frametest.model.response.VersionResponse;
import com.frame.wangyu.retrofitframe.model.tulin.response.TuLingResponse;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by wangyu on 2019/4/24.
 */

public interface TestApi {
//    @Headers("apikey:81bf9da930c7f9825a3c3383f1d8d766")
    @GET("/yy-face/version")
    Observable<VersionResponse> getVersion();
}
