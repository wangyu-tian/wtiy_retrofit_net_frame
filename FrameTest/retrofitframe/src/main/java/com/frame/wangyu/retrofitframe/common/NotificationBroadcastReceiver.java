package com.frame.wangyu.retrofitframe.common;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.frame.wangyu.retrofitframe.util.DownloadNotificationUtil.NOTIFICATION_CANCELLED;
import static com.frame.wangyu.retrofitframe.util.DownloadNotificationUtil.NOTIFICATION_CLICKED;

/**
 * Created by wangyu on 2019/4/26.
 * 广播处理notification的点击和滑动删除事件
 */

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    public static final String TYPE = "type"; //这个type是为了Notification更新信息的，这个不明白的朋友可以去搜搜，很多

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        int type = intent.getIntExtra(TYPE, -1);

        if (type != -1) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(type);
        }

        if (action.equals(NOTIFICATION_CLICKED)) {
            //处理点击事件
        }

        if (action.equals(NOTIFICATION_CANCELLED)) {
            //处理滑动清除和点击删除事件
        }
    }
}