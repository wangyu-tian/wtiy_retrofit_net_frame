package com.frame.wangyu.retrofitframe.common;

/**
 * Created by wangyu on 2019/4/25.
 * 文件下载监听器
 */


public interface DownloadListener {
    //开始下载
    void onStart();
    //正在下载
    void onProgress(int currentLength);
    //完成下载
    void onFinish(String localPath);
    //下载失败
    void onFailure();
    //取消下载
    void onCancel();
    //暂停下载
    void onPause();
}