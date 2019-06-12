1.进行封装之后调用方式很简单（此处测试图灵的一个post请求）

初始化代码

WTApplicationContextUtil.initContext(mContext);//配置初始化

RetrofitModel.getInstance().aiTuLing("武松打虎",
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
2.增加文件下载进度条显示

/**
 * 默认文件下载模式
 * @param context
 * @param url 文件路径
 * @param savePath 保存路径
 * @param isShowDialog 是否显示下载进度条
 * @param isCanCancel 是否可以对进度条进行任意取消

 * @param isContinue 是否可以断点下载

 * @param downloadListener 下载进度监听器

 */
DownloadUtil.getInstance().downloadFileDefault(mContext,"/yy-face/images/test.mp4",null
        ,true,false,true,null);
3.多文件任务同时下载

保存任务对象到SharedPreferences中。



github地址：https://github.com/yiyuan-wangyu/wtiy_retrofit_net_frame

查看版本号：https://jitpack.io/#yiyuan-wangyu/wtiy_retrofit_net_frame

引入方式也很简单：

allprojects {
    repositories {
        google()
        jcenter()
        maven { url "https://jitpack.io" }
    }
}

implementation 'com.github.yiyuan-wangyu:wtiy_retrofit_net_frame:0.4'
implementation 'io.reactivex:rxjava:1.3.0'
implementation 'io.reactivex:rxandroid:1.1.0'
implementation 'com.squareup.retrofit2:retrofit:2.3.0'
implementation 'com.squareup.retrofit2:converter-gson:2.3.0'
implementation 'com.squareup.retrofit2:adapter-rxjava:2.3.0'
