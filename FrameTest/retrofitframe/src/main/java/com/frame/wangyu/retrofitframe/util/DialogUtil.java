package com.frame.wangyu.retrofitframe.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.frame.wangyu.retrofitframe.R;

/**
 * Created by wangyu on 2019/4/25.
 * 对话框工具类
 */

public class DialogUtil {

    /**
     * 确认对话框
     * @param context
     * @param text
     * @param feedBack
     */
    public static void confirmDialog(final Context context,String text,final FeedBack feedBack){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.notification);
        builder.setTitle(text);
        builder.setPositiveButton(context.getString(R.string.dialog_sure), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                feedBack.onSure();
            }
        });
        builder.setNegativeButton(context.getString(R.string.dialog_no), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                feedBack.onNo();
            }
        });
        builder.show();
    }

    public interface FeedBack {
        void onSure();

        void onNo();
    }
}
