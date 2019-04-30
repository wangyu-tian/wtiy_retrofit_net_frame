package com.frame.wangyu.frametest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by wangyu on 2019/4/30.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected static final String TAG = "BaseActivity";
    private AlertDialog dialog;
    protected Context mContext;
    public Intent mIntent;

    public BaseActivity() {
    }

    protected void onCreate(@Nullable Bundle var1) {
        super.onCreate(var1);
        this.mContext = this;
        this.mIntent = new Intent();
        this.setContentView(this.getLayoutId());

        try {
            this.initViews();
            this.initData();
            this.initListeners();
        } catch (Exception var3) {
            Log.e("BaseActivity", var3.getMessage());
        }

    }

    public abstract int getLayoutId();

    public abstract void initViews();

    public abstract void initData();

    public abstract void initListeners();

    public String getVersionName() {
        return null != this.getPackageInfo()?this.getPackageInfo().versionName:"";
    }

    public String getStringValue(int var1) {
        return this.getResources().getString(var1);
    }

    private PackageInfo getPackageInfo() {
        PackageManager var1 = this.getPackageManager();

        try {
            return var1.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException var3) {
            Log.d("BaseActivity", var3.getMessage());
            return null;
        }
    }



}
