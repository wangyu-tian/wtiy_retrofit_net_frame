package com.frame.wangyu.retrofitframe.common;

/**
 * Created by wangyu on 2019/4/25.
 * 文件下载监听器
 */


public interface DownloadListener {
    void onStart();

    void onProgress(int currentLength);

    void onFinish(String localPath);

    void onFailure();
}