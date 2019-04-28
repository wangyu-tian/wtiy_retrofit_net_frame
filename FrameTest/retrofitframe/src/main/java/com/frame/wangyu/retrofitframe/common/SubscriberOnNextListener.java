package com.frame.wangyu.retrofitframe.common;

/**
 * Copyright © 2018 Yiyuan Networks 上海义援网络科技有限公司. All rights reserved.
 *
 * @author Created by Wangpeng on 2018/5/22 15:43.
 */
public interface SubscriberOnNextListener<T> {
    
    /**
     * next
     *
     * @param t
     */
    void onNext(T t);
    
    /**
     * error
     *
     * @param e
     */
    void onError(Throwable e);
}
