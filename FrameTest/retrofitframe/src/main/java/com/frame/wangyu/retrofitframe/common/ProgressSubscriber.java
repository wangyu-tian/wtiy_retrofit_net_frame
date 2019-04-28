package com.frame.wangyu.retrofitframe.common;

import android.content.Context;


import com.frame.wangyu.retrofitframe.util.LogUtils;

import rx.Subscriber;

/**
 * Copyright © 2018 Yiyuan Networks 上海义援网络科技有限公司. All rights reserved.
 *
 * @author Created by Wangpeng on 2018/5/22 15:43.
 */
public class ProgressSubscriber<T> extends Subscriber<T> implements ProgressCancelListener {
    
    private static final String TAG = "ProgressSubscriber";
    private SubscriberOnNextListener<T> mSubscriberOnNextListener;
    private ProgressDialogHandler mProgressDialogHandler;
    
    private Context context;
    
    public ProgressSubscriber(SubscriberOnNextListener<T> mSubscriberOnNextListener, Context context) {
        this(mSubscriberOnNextListener,context,true);
    }

    public ProgressSubscriber(SubscriberOnNextListener<T> mSubscriberOnNextListener, Context context,boolean isShowDialog) {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        this.context = context;
        if(isShowDialog) {
            mProgressDialogHandler = new ProgressDialogHandler(context, this, true);
        }
    }
    
    
    private void showProgressDialog() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG).sendToTarget();
        }
    }
    
    private void dismissProgressDialog() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG).sendToTarget();
            mProgressDialogHandler = null;
        }
    }
    
    @Override
    public void onStart() {
        showProgressDialog();
    }
    
    @Override
    public void onCompleted() {
        dismissProgressDialog();
        //Toast.makeText(context, "Completed", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onError(Throwable e) {
        
        LogUtils.e(TAG, "onError: " + e.getMessage());
        dismissProgressDialog();
        mSubscriberOnNextListener.onError(e);
    }
    
    @Override
    public void onNext(T t) {
        dismissProgressDialog();
        mSubscriberOnNextListener.onNext(t);
    }
    
    @Override
    public void onCancelProgress() {
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }
    }
    
}
