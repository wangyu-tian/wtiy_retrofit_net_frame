package com.frame.wangyu.retrofitframe;

import android.Manifest;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.frame.wangyu.retrofitframe.common.LogInterceptor;
import com.frame.wangyu.retrofitframe.util.LogUtils;
import com.frame.wangyu.retrofitframe.util.PermissionUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.frame.wangyu.retrofitframe.constant.RetrofitConfig.BASE_URL;

/**
 * Created by wangyu on 2019/4/24.
 */
public class RetrofitSingle {

    private final int CONNECT_TIME_OUT = 30;
    private final int WRITE_TIME_OUT = 30;
    private final int READ_TIME_OUT = 30;
    private Retrofit retrofit;
    private RetrofitSingle(boolean isLogInterceptor) {
        retrofit = getRetrofitBase(isLogInterceptor,null);
    }

    private Retrofit getRetrofitBase(boolean isLogInterceptor,String baseUrl) {
        //权限请求
        OkHttpClient.Builder clientBuild = null;
        if(isLogInterceptor) {
            clientBuild = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                @Override
                public Response intercept(@NonNull Chain chain) throws IOException {
                    Request request = chain.request();
                    return chain.proceed(request);
                }
            }).addInterceptor(new LogInterceptor());
        }else{
            clientBuild = new OkHttpClient.Builder();
        }
        OkHttpClient client = clientBuild.connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS).writeTimeout
                (WRITE_TIME_OUT, TimeUnit.SECONDS).readTimeout(READ_TIME_OUT, TimeUnit.SECONDS).build();
        return  new Retrofit.Builder().client(client).addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).baseUrl(TextUtils.isEmpty(baseUrl)?BASE_URL:baseUrl).build();
    }


    /**
     * 请求权限
     * @param context
     */
    public void permissionRetrofit(Context context){
        if(!SingletonInstance.mIsGranted) {
            PermissionUtils.requestPermissions(context, 0x01,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET}, new PermissionUtils.OnPermissionListener() {
                        @Override
                        public void onPermissionGranted() {
                            SingletonInstance.mIsGranted = true;
                        }

                        @Override
                        public void onPermissionDenied(String[] deniedPermissions) {
                            SingletonInstance.mIsGranted = false;
                        }
                    });
        }
    }

    public <T> T getRetrofitApi(Class<T>  classObject){
        return retrofit.create(classObject);
    }

    /**
     *
     * @param classObject API接口对象
     * @param isFile 是否为文件下载
     * @param baseUrl 地址
     * @param <T>
     * @return
     */
    public <T> T getRetrofitApi(Class<T>  classObject,boolean isFile,String baseUrl){
        return getRetrofitBase(!isFile,baseUrl).create(classObject);
    }

    public void toSubscribe(Observable observable, Subscriber subscriber){
        observable.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers
                .mainThread()).subscribe(subscriber);
    }

    private static class SingletonInstance {
        private static final RetrofitSingle INSTANCE = new RetrofitSingle(true);
        private static final RetrofitSingle INSTANCE_STREAM = new RetrofitSingle(false);
        private static boolean mIsGranted = false;
    }

    /**
     * 普通文本接口
     * @return
     */
    public static RetrofitSingle getInstance() {
        return SingletonInstance.INSTANCE;
    }

    /**
     * 文件流下载接口
     * @return
     */
    public static RetrofitSingle getInstanceStream() {
        return SingletonInstance.INSTANCE_STREAM;
    }
}
