package com.frame.wangyu.frametest;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.frame.wangyu.retrofitframe.RetrofitSingle;
import com.frame.wangyu.retrofitframe.common.ProgressSubscriber;
import com.frame.wangyu.retrofitframe.common.SubscriberOnNextListener;
import com.frame.wangyu.retrofitframe.constant.RetrofitConfig;
import com.frame.wangyu.retrofitframe.model.tulin.RetrofitModel;
import com.frame.wangyu.retrofitframe.model.tulin.response.TuLingResponse;

public class RoBotMainActivity extends AppCompatActivity {

    private Context mContext;

    TextView tvShow;
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot_main);
        mContext = this;

        final EditText editText = findViewById(R.id.edit);
        Button button = findViewById(R.id.send);
        tvShow = findViewById(R.id.tv_show);
        count = RetrofitConfig.downloadModelList.size();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askTuLing(editText.getText().toString());
            }
        });

    }


    private void askTuLing(String text) {
        RetrofitModel.getInstance().aiTuLing(text,
                new ProgressSubscriber<>(new SubscriberOnNextListener<TuLingResponse>() {
                    @Override
                    public void onNext(TuLingResponse tuLingResponse) {
                        tvShow.setText(tuLingResponse.text);
                    }

                    @Override
                    public void onError(Throwable e) {
                        tvShow.setText(e.getMessage());
                    }
                },mContext));
    }


}
