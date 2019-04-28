package com.frame.wangyu.retrofitframe.api;

import com.frame.wangyu.retrofitframe.model.tulin.response.TuLingResponse;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by wangyu on 2019/4/24.
 */

public interface TuLingApi{
//    @Headers("apikey:81bf9da930c7f9825a3c3383f1d8d766")
    @POST("/openapi/api")
    @FormUrlEncoded
    Observable<TuLingResponse> getMessageByTuLing(@HeaderMap Map<String,String> headers, @FieldMap Map<String,String> params);
}
