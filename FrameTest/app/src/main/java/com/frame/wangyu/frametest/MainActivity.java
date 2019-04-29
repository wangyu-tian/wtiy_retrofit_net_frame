package com.frame.wangyu.frametest;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.frame.wangyu.frametest.model.VersionModel;
import com.frame.wangyu.frametest.model.response.VersionResponse;
import com.frame.wangyu.retrofitframe.RetrofitSingle;
import com.frame.wangyu.retrofitframe.WTApplicationContextUtil;
import com.frame.wangyu.retrofitframe.common.DownloadListener;
import com.frame.wangyu.retrofitframe.common.ProgressSubscriber;
import com.frame.wangyu.retrofitframe.common.SubscriberOnNextListener;
import com.frame.wangyu.retrofitframe.constant.RetrofitConfig;
import com.frame.wangyu.retrofitframe.model.tulin.RetrofitModel;
import com.frame.wangyu.retrofitframe.model.tulin.response.TuLingResponse;
import com.frame.wangyu.retrofitframe.util.DialogUtil;
import com.frame.wangyu.retrofitframe.util.DownloadUtil;
import com.frame.wangyu.retrofitframe.util.model.DownloadModel;

public class MainActivity extends AppCompatActivity {

    private Context mContext;

    private LinearLayout linearLayout;
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        WTApplicationContextUtil.initContext(mContext);
        RetrofitSingle.getInstance().permissionRetrofit(mContext);
        final EditText editText = findViewById(R.id.edit);
        Button button = findViewById(R.id.send);
        linearLayout = findViewById(R.id.ll_down);
        count = RetrofitConfig.downloadModelList.size();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askTuLing(editText.getText().toString());
            }
        });

        Button buttonD = findViewById(R.id.download);
        buttonD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downFile();
            }
        });
        initDownloadView();
    }

    private void initDownloadView() {
        for (final DownloadModel downloadModel : RetrofitConfig.downloadModelList){
            final TextView textView = new TextView(this);
            textView.setText(downloadModel.fileName+"下载进度为"+downloadModel.progress+"类型为"+downloadModel.downType);
            linearLayout.addView(textView);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DownloadUtil.getInstance(downloadModel).downloadFileDefault(mContext,downloadModel.downUrl,null
                            ,downloadModel.fileName,false,false,true,new DownloadListener() {
                                @Override
                                public void onStart() {
                                    textView.setText(downloadModel.fileName+"下载进度为"+0+"类型为"+1);
                                }

                                @Override
                                public void onProgress(int currentLength) {
                                    textView.setText(downloadModel.fileName+"下载进度为"+currentLength+"类型为"+1);
                                }

                                @Override
                                public void onFinish(String localPath) {
                                }

                                @Override
                                public void onFailure() {

                                }

                                @Override
                                public void onCancel() {

                                }

                                @Override
                                public void onPause() {

                                }
                            });
                }
            });
        }

    }

    private void downFile() {
        final String fileName = count+++"测试视频.mp4";
        final TextView textView = new TextView(this);

        DownloadUtil.getInstance().downloadFileDefault(mContext, "/yy-face/images/test.mp4", null
                , fileName, true, false, true, new DownloadListener() {
                    @Override
                    public void onStart() {
                        textView.setText(fileName+"下载进度为"+0+"类型为"+1);
                    }

                    @Override
                    public void onProgress(int currentLength) {
                        textView.setText(fileName+"下载进度为"+currentLength+"类型为"+1);
                    }

                    @Override
                    public void onFinish(String localPath) {
                    }

                    @Override
                    public void onFailure() {

                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onPause() {

                    }
                });
        linearLayout.addView(textView);
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

    public void checkVersion(){
        PackageInfo pkg = null;
        try {
            pkg = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            Toast.makeText(mContext, "版本："+pkg.versionCode,Toast.LENGTH_LONG).show();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

}
