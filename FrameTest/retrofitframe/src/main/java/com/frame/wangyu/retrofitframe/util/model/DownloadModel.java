package com.frame.wangyu.retrofitframe.util.model;

/**
 * Created by wangyu on 2019/4/29.
 */

public class DownloadModel {
    public int id;//标志
    public String mFilePath; //下载到本地的文件路径
    public int progress;//下载进度
    public long size;//文件大小
    public int downType;//文件下载类型DownloadEnum
    public String downUrl;//文件下载路径
    public String fileName;//文件名
}
