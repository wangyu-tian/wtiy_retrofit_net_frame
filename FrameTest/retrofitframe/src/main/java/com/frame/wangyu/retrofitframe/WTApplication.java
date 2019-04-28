package com.frame.wangyu.retrofitframe;

import android.app.Application;
import android.content.Context;

/**
 * Created by wangyu on 2019/4/26.
 */

public class WTApplication extends Application{

    public static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }
}
