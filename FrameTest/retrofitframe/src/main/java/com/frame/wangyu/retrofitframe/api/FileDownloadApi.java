package com.frame.wangyu.retrofitframe.api;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by wangyu on 2019/4/25.
 * 文件下载公共处理
 */

public interface FileDownloadApi {
    /**
     * 下载视频
     *
     * @param fileUrl
     * @return
     */
    @Streaming //大文件时要加不然会OOM
    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);

    @Streaming //大文件时要加不然会OOM
    @GET
    Call<ResponseBody> downloadFileContinue(@HeaderMap Map<String,String> headers, @Url String fileUrl);
}
