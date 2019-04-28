package com.frame.wangyu.frametest;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.frame.wangyu.frametest.model.VersionModel;
import com.frame.wangyu.frametest.model.response.VersionResponse;
import com.frame.wangyu.retrofitframe.RetrofitSingle;
import com.frame.wangyu.retrofitframe.common.DownloadListener;
import com.frame.wangyu.retrofitframe.common.ProgressSubscriber;
import com.frame.wangyu.retrofitframe.common.SubscriberOnNextListener;
import com.frame.wangyu.retrofitframe.model.tulin.RetrofitModel;
import com.frame.wangyu.retrofitframe.model.tulin.response.TuLingResponse;
import com.frame.wangyu.retrofitframe.util.DialogUtil;
import com.frame.wangyu.retrofitframe.util.DownloadUtil;
import com.frame.wangyu.retrofitframe.util.LogUtils;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        RetrofitSingle.getInstance().permissionRetrofit(mContext);
        final EditText editText = findViewById(R.id.edit);
        Button button = findViewById(R.id.send);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askVersion();
            }
        });

        PackageInfo pkg = null;
        try {
            pkg = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            Toast.makeText(mContext, "版本："+pkg.versionCode,Toast.LENGTH_LONG).show();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }
    private void downFile() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DownloadUtil.getInstance().downloadFileDefault(mContext,"/yy-face/images/test.mp4",null
                        ,"测试视频.mp4",true,false,true);
            }
        });
    }

    private void askVersion() {
        VersionModel.getInstance().queryVersion(
                new ProgressSubscriber<>(new SubscriberOnNextListener<VersionResponse>() {
                    @Override
                    public void onNext(VersionResponse versionResponse) {
                        DialogUtil.confirmDialog(mContext,"检查到新版本，是否现在进行更新", new DialogUtil.FeedBack() {
                            @Override
                            public void onSure() {
                                downFile();
                            }

                            @Override
                            public void onNo() {

                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(mContext,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                },mContext));
    }

    private void askTuLing(String text) {
        RetrofitModel.getInstance().aiTuLing(text,
                new ProgressSubscriber<>(new SubscriberOnNextListener<TuLingResponse>() {
                    @Override
                    public void onNext(TuLingResponse tuLingResponse) {
                        Toast.makeText(mContext,tuLingResponse.code+":"+tuLingResponse.text,Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(mContext,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                },mContext));
    }

}
