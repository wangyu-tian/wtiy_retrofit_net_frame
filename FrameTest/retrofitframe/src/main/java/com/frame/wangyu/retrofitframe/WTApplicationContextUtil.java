package com.frame.wangyu.retrofitframe;

import android.content.Context;

import com.frame.wangyu.retrofitframe.constant.RetrofitConfig;
import com.frame.wangyu.retrofitframe.util.DownloadUtil;
import com.frame.wangyu.retrofitframe.util.SharedPreferencesUtils;
import com.frame.wangyu.retrofitframe.util.model.DownloadModel;

import java.util.List;

/**
 * Created by wangyu on 2019/4/26.
 */

public class WTApplicationContextUtil {

    public static Context mContext;

    public static void initContext(Context context){
        mContext = context;
        initData();
    }

    /**
     * 初始化一些配置信息
     */
    private static void initData() {
        List<DownloadModel> downloadModelList = SharedPreferencesUtils.getDownloadUtilList(RetrofitConfig.DOWNLOAD_FILE_SHARE_SAVE);
        if(downloadModelList != null && downloadModelList.size() >0){
            RetrofitConfig.downloadModelList = downloadModelList;
        }
    }
}
