package com.frame.wangyu.retrofitframe.common;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

/**
 * Copyright © 2018 Yiyuan Networks 上海义援网络科技有限公司. All rights reserved.
 *
 * @author Created by Wangpeng on 2018/5/22 15:43.
 */
public class ProgressDialogHandler extends Handler {
    
    public static final int SHOW_PROGRESS_DIALOG = 1;
    public static final int DISMISS_PROGRESS_DIALOG = 2;
    
    private CustomProgressDialog pd;
    
    private Context context;
    private boolean cancelable;
    private ProgressCancelListener mProgressCancelListener;
    
    ProgressDialogHandler(Context context, ProgressCancelListener mProgressCancelListener, boolean cancelable) {
        super();
        this.context = context;
        this.mProgressCancelListener = mProgressCancelListener;
        this.cancelable = cancelable;
    }
    
    private void initProgressDialog() {
        if (pd == null) {
            //pd = new ProgressDialog(context);
            pd = CustomProgressDialog.createDialog(context);
            
            pd.setCancelable(cancelable);
            
            if (cancelable) {
                pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        if (mProgressCancelListener != null) {
                            mProgressCancelListener.onCancelProgress();
                        }
                    }
                });
            }
            
            if (!pd.isShowing()) {
                pd.show();
            }
        }
    }
    
    private void dismissProgressDialog() {
        if (pd != null) {
            pd.dismiss();
            pd = null;
        }
    }
    
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case SHOW_PROGRESS_DIALOG:
                initProgressDialog();
                break;
            case DISMISS_PROGRESS_DIALOG:
                dismissProgressDialog();
                break;
            default:
                break;
        }
    }
}
