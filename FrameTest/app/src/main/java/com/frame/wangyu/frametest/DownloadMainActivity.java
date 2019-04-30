package com.frame.wangyu.frametest;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.frame.wangyu.retrofitframe.common.DownloadListener;
import com.frame.wangyu.retrofitframe.constant.DownloadEnum;
import com.frame.wangyu.retrofitframe.constant.RetrofitConfig;
import com.frame.wangyu.retrofitframe.util.DownloadUtil;
import com.frame.wangyu.retrofitframe.util.ToastUtil;
import com.frame.wangyu.retrofitframe.util.model.DownloadModel;

import java.util.List;

public class DownloadMainActivity extends AppCompatActivity {

    private Context mContext;

    RecyclerView mRecyclerView;

    ActivityAdapter aiActivityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_main);
        mContext = this;
        mRecyclerView = findViewById(R.id.rv_down);
        LinearLayoutManager lm = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(lm);
        Button buttonD = findViewById(R.id.download);
        buttonD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downFile(null,"/yy-face/images/test.mp4",(int)(Math.random()*10000)+"测试视频.mp4");
            }
        });
        aiActivityAdapter = new DownloadMainActivity.ActivityAdapter(RetrofitConfig.downloadModelList);
        mRecyclerView.setAdapter(aiActivityAdapter);
        createDownTask();
    }

    private void createDownTask() {
        for (DownloadModel downloadModel:RetrofitConfig.downloadModelList) {
            downloadModel.downType = DownloadEnum.DownloadPause.getCode();
            downFile(downloadModel,downloadModel.downUrl,downloadModel.fileName);
        }
    }


    private void downFile(DownloadModel downloadModel,final String url,final String fileName) {
        final DownloadUtil downloadUtil = DownloadUtil.getInstance("http://192.168.1.166:8090",downloadModel);
        if(downloadModel == null)downloadModel = downloadUtil.getDownloadModel();
        final DownloadModel downloadModelImportant = downloadModel;
        downloadUtil.downloadFileDefault(mContext, url, null
                , fileName, false, false, true, new DownloadListener() {
                    @Override
                    public void onStart() {
                        updateNotifyData();
                    }

                    @Override
                    public void onProgress(final int currentLength) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((TextView)aiActivityAdapter.getViewByPosition(mRecyclerView,aiActivityAdapter.getData().indexOf(downloadModelImportant),R.id.progress))
                                        .setText(currentLength+"%");
                            }
                        });
                        downloadUtil.getDownloadModel().progress = currentLength;
                    }

                    @Override
                    public void onFinish(String localPath) {
                        updateNotifyData();
                    }

                    @Override
                    public void onFailure() {
                        updateNotifyData();
                    }

                    @Override
                    public void onCancel() {
                        updateNotifyData();
                    }

                    @Override
                    public void onPause() {
                        updateNotifyData();
                    }
                });
    }

    public void updateNotifyData(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                aiActivityAdapter.notifyDataSetChanged();
            }
        });
    }



    class ActivityAdapter extends BaseItemDraggableAdapter<DownloadModel, BaseViewHolder> {
        public ActivityAdapter(List<DownloadModel> downloadModelList) {
            super(R.layout.item_download_activity, downloadModelList);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final DownloadModel item) {
            if (item != null) {
                helper.setText(R.id.fileName,item.fileName);
                helper.setText(R.id.fileSize,item.size/1024.0f/1024.0f+"MB");
                helper.setText(R.id.progress,item.progress+"%");
                if (item.downType == DownloadEnum.Downloading.getCode()) {
                    helper.setText(R.id.download_pause, "暂停");
                    helper.getView(R.id.download_pause).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            item.downType = DownloadEnum.DownloadPause.getCode();
                            helper.setText(R.id.download_pause, "继续");
                        }
                    });
                } else if (item.downType == DownloadEnum.DownloadPause.getCode()) {
                    helper.setText(R.id.download_pause, "继续");
                    helper.getView(R.id.download_pause).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            item.downType = DownloadEnum.Downloading.getCode();
                            downFile(item, item.downUrl, item.fileName);
                            helper.setText(R.id.download_pause, "暂停");
                        }
                    });
                }
                helper.getView(R.id.download_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(item.downType == DownloadEnum.Downloading.getCode()) {
                            item.downType = DownloadEnum.DownloadCancel.getCode();
                        }else{
                            aiActivityAdapter.getData().remove(item);
                            aiActivityAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }
    }

}
