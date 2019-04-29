package com.frame.wangyu.retrofitframe;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.frame.wangyu.retrofitframe.common.ProgressSubscriber;
import com.frame.wangyu.retrofitframe.common.SubscriberOnNextListener;
import com.frame.wangyu.retrofitframe.model.tulin.RetrofitModel;
import com.frame.wangyu.retrofitframe.model.tulin.response.TuLingResponse;
import com.frame.wangyu.retrofitframe.util.DownloadUtil;

/**
 * Created by wangyu on 2019/4/25.
 */

public class Test extends Activity {
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        mContext = this;
        //请求权限，建议放在application中
        RetrofitSingle.getInstance().permissionRetrofit(mContext);
//        final EditText editText = findViewById(R.id.edit);
//        Button button = findViewById(R.id.send);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                downFile();
//            }
//        });
    }
    private void downFile() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DownloadUtil.getInstance().downloadFileDefault(mContext,"/yy-face/images/test.mp4",null,
                        "测试视频.mp4",true,false,true,null);
            }
        });
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
