package com.frame.wangyu.frametest;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.frame.wangyu.retrofitframe.RetrofitSingle;
import com.frame.wangyu.retrofitframe.WTApplicationContextUtil;
import com.frame.wangyu.retrofitframe.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangyu on 2019/4/30.
 */

public class WTMainActivity extends BaseActivity {

    RecyclerView mRecyclerView;

    AiActivityAdapter aiActivityAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_wt_main;
    }

    @Override
    public void initViews() {
        mRecyclerView = findViewById(R.id.ai_rlv);
    }

    @Override
    public void initData() {
        WTApplicationContextUtil.initContext(mContext);
        RetrofitSingle.getInstance().permissionRetrofit(mContext);
        List<String> mainList = initStringList();
        GridLayoutManager lm = new GridLayoutManager(WTMainActivity.this, 2);
        mRecyclerView.setLayoutManager(lm);
        aiActivityAdapter = new AiActivityAdapter(mainList);
        mRecyclerView.setAdapter(aiActivityAdapter);
    }

    private List<String> initStringList() {
        List<String> mainList = new ArrayList<>();
        mainList.add("智能机器人");
        mainList.add("下载管理");
        return mainList;
    }


    class AiActivityAdapter extends BaseItemDraggableAdapter<String, BaseViewHolder> {
        public AiActivityAdapter(List<String> strings) {
            super(R.layout.item_activity, strings);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            if (item != null) {
                helper.setText(R.id.activity,item);
            }
        }
    }

    @Override
    public void initListeners() {
        aiActivityAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (position){
                    case 0://智能机器人
                        startActivity(new Intent(WTMainActivity.this,RoBotMainActivity.class));
                        break;
                    case 1://下载管理
                        startActivity(new Intent(WTMainActivity.this,DownloadMainActivity.class));
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
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