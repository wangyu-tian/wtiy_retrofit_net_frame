package com.frame.wangyu.retrofitframe.constant;

/**
 * Created by wangyu on 2019/4/29.
 */

public enum  DownloadEnum {
    Downloading(1,"下载中"),
    DownloadFinish(2,"下载完成"),
    DownloadFail(3,"下载失败"),
    DownloadPause(4,"下载取消"),
    DownloadCancel(5,"下载暂停");

    private String msg;
    private int code;
    DownloadEnum(int code,String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }
}
