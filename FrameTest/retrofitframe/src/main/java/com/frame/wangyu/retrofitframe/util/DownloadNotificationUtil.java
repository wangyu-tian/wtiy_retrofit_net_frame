package com.frame.wangyu.retrofitframe.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.frame.wangyu.retrofitframe.R;
import com.frame.wangyu.retrofitframe.common.NotificationBroadcastReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.frame.wangyu.retrofitframe.constant.RetrofitConfig.DOWNLOAD_CHANNEL_ID;

/**
 * 下载通知工具类
 */
public  class DownloadNotificationUtil {

    private PendingIntent pendingIntentClick;
    private PendingIntent pendingIntentCancel;
    private int NOTICE_DOWNLOAD_ID;

    public final static String NOTIFICATION_CLICKED = "notification_clicked";

    public final static String NOTIFICATION_CANCELLED = "notification_cancelled";

    public DownloadNotificationUtil(int downLoadId){
        NOTICE_DOWNLOAD_ID = downLoadId;
    }

    public  void sendDefaultNotice(Context context, String title, String desc, int progress) {
        clickNotification(context);
        String id = DOWNLOAD_CHANNEL_ID;//下载通道
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(id, title, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
            notification = new Notification.Builder(context)
                    .setChannelId(id)
                    .setContentTitle(title)
                    .setContentText(desc)
                    .setProgress(100, progress, false)
                    .setContentIntent(pendingIntentClick)
                    .setDeleteIntent(pendingIntentCancel)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.download_icon))
                    .setSmallIcon(R.drawable.download_icon).build();
        } else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setContentTitle(title)
                    .setContentText(desc)
                    .setProgress(100, progress, false)
                    .setSmallIcon(R.drawable.download_icon)
                    .setContentIntent(pendingIntentClick)
                    .setDeleteIntent(pendingIntentCancel)
                    .setOngoing(true);
            notification = notificationBuilder.build();
        }
//        MediaPlayer mMediaPlayer= MediaPlayer.create(context, R.raw.n84146);
//        mMediaPlayer.start();
        notificationManager.notify(NOTICE_DOWNLOAD_ID, notification);


    }

    private  void clickNotification(Context context) {
        Intent intentClick = new Intent(context, NotificationBroadcastReceiver.class);
        intentClick.setAction(NOTIFICATION_CLICKED);
        intentClick.putExtra(NotificationBroadcastReceiver.TYPE, "click");
        pendingIntentClick = PendingIntent.getBroadcast(context, 0, intentClick, PendingIntent.FLAG_ONE_SHOT);

        Intent intentCancel = new Intent(context, NotificationBroadcastReceiver.class);
        intentCancel.setAction(NOTIFICATION_CANCELLED);
        intentCancel.putExtra(NotificationBroadcastReceiver.TYPE, "cancel");
        pendingIntentCancel = PendingIntent.getBroadcast(context, 0, intentCancel, PendingIntent.FLAG_ONE_SHOT);
    }

    public  void cancelNotification(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(NOTICE_DOWNLOAD_ID);
    }

    private  String getTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.SIMPLIFIED_CHINESE);
        return format.format(new Date());
    }

    public  boolean isDarkNotificationTheme(Context context) {
        return !isSimilarColor(Color.BLACK, getNotificationColor(context));
    }

    /**
     * 获取通知栏颜色
     */
    @SuppressWarnings("deprecation")
    public  int getNotificationColor(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder.build();
        int layoutId = notification.contentView.getLayoutId();
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(layoutId, null, false);
        if (viewGroup.findViewById(android.R.id.title) != null) {
            return ((TextView) viewGroup.findViewById(android.R.id.title)).getCurrentTextColor();
        }
        return findColor(viewGroup);
    }

    private  int findColor(ViewGroup viewGroupSource) {
        int color = Color.TRANSPARENT;
        LinkedList<ViewGroup> viewGroups = new LinkedList<>();
        viewGroups.add(viewGroupSource);
        while (viewGroups.size() > 0) {
            ViewGroup viewGroup1 = viewGroups.getFirst();
            for (int i = 0; i < viewGroup1.getChildCount(); i++) {
                if (viewGroup1.getChildAt(i) instanceof ViewGroup) {
                    viewGroups.add((ViewGroup) viewGroup1.getChildAt(i));
                } else if (viewGroup1.getChildAt(i) instanceof TextView) {
                    if (((TextView) viewGroup1.getChildAt(i)).getCurrentTextColor() != -1) {
                        color = ((TextView) viewGroup1.getChildAt(i)).getCurrentTextColor();
                    }
                }
            }
            viewGroups.remove(viewGroup1);
        }
        return color;
    }

    private  boolean isSimilarColor(int baseColor, int color) {
        int simpleBaseColor = baseColor | 0xff000000;
        int simpleColor = color | 0xff000000;
        int baseRed = Color.red(simpleBaseColor) - Color.red(simpleColor);
        int baseGreen = Color.green(simpleBaseColor) - Color.green(simpleColor);
        int baseBlue = Color.blue(simpleBaseColor) - Color.blue(simpleColor);
        double value = Math.sqrt(baseRed * baseRed + baseGreen * baseGreen + baseBlue * baseBlue);
        if (value < 180.0) {
            return true;
        }
        return false;
    }
}
