package com.frame.wangyu.retrofitframe.util;

/**
 * Created by wangyu on 2019/4/25.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.frame.wangyu.retrofitframe.R;
import com.frame.wangyu.retrofitframe.RetrofitSingle;
import com.frame.wangyu.retrofitframe.api.FileDownloadApi;
import com.frame.wangyu.retrofitframe.common.DownloadListener;
import com.frame.wangyu.retrofitframe.constant.DownloadEnum;
import com.frame.wangyu.retrofitframe.util.model.DownloadModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.frame.wangyu.retrofitframe.constant.RetrofitConfig.DOWNLOAD_FILE_SHARE_PRE;
import static com.frame.wangyu.retrofitframe.constant.RetrofitConfig.DOWNLOAD_FILE_SHARE_SAVE;
import static com.frame.wangyu.retrofitframe.constant.RetrofitConfig.NOTICE_DOWNLOAD_ID;
import static com.frame.wangyu.retrofitframe.constant.RetrofitConfig.downloadModelList;

/**
 * Description：Retrofit下载文件工具类
 */

public class DownloadUtil {

    public static DownloadUtil getInstance() {
        return new DownloadUtil();
    }

    public static DownloadUtil getInstance(DownloadModel downloadModel) {
        DownloadUtil downloadUtil =  new DownloadUtil();
        downloadUtil.downloadModel = downloadModel;
        return downloadUtil;
    }

    public static DownloadUtil getInstance(String baseUrl) {
        return new DownloadUtil(baseUrl);
    }

    public static DownloadUtil getInstance(String baseUrl,DownloadModel downloadModel) {
        if(downloadModel == null)
            return getInstance(baseUrl);
        DownloadUtil downloadUtil =  new DownloadUtil(baseUrl);
        downloadUtil.downloadModel = downloadModel;
        return downloadUtil;
    }

    private static final String TAG = "DownloadUtil";
    private static String PATH_CHALLENGE_FILE;
    private static final String SAVE_PATH_FOLDER_DEFAULT = "/retrofit_wtiy";
    //视频下载相关
    protected FileDownloadApi mApi;
    private Call<ResponseBody> mCall;
    private File mFile;
    private Thread mThread;
    private DownloadModel downloadModel;
    private ProgressDialog progressDialog;
    private DownloadListener downloadListenerCustomer;

    public DownloadModel getDownloadModel() {
        return downloadModel;
    }

    private DownloadUtil() {
        downloadModel = new DownloadModel();
        if (mApi == null) {
            //初始化网络请求接口
            mApi =  RetrofitSingle.getInstanceStream().getRetrofitApi(FileDownloadApi.class);
        }
    }

    private DownloadUtil(String baseUrl) {
        downloadModel = new DownloadModel();
        if (mApi == null) {
            //初始化网络请求接口
            mApi =  RetrofitSingle.getInstanceStream().getRetrofitApi(FileDownloadApi.class,true,baseUrl);
        }
    }

    /**
     * 默认文件下载模式
     * @param context
     * @param url 文件路径
     * @param savePath 保存路径
     * @param fileName 文件名
     * @param isShowDialog 是否显示下载进度条
     * @param isCanCancel 是否可以对进度条进行任意取消
     * @param isContinue 是否支持断点续传
     * @param downloadListener 下载进度监听器
     */
    public void downloadFileDefault(final Context context, String url, String savePath, final String fileName, boolean isShowDialog, boolean isCanCancel,boolean isContinue,DownloadListener downloadListener){
        downloadModel.downUrl = url;
        downloadListenerCustomer = downloadListener;
        if(isShowDialog) {
            progressDialog = new ProgressDialog(context);//实例化ProgressDialog
            progressDialog.setMax(100);//设置最大值
            progressDialog.setTitle(context.getString(R.string.file_download_title));//设置标题
            progressDialog.setIcon(R.drawable.download_icon);//设置标题小图标
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//设置样式为横向显示进度的样式
            progressDialog.setMessage(context.getString(R.string.file_download_msg));
            progressDialog.incrementProgressBy(0);//设置初始值为0，其实可以不用设置，默认就是0
            progressDialog.setIndeterminate(false);//是否精确显示对话框，false为是，反之为否
            progressDialog.setCancelable(isCanCancel);
            progressDialog.setButton(DialogInterface.BUTTON_POSITIVE,context.getString(R.string.file_download_back),new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ToastUtil.showMessage(context.getString(R.string.file_download_back_info));
                }
            });
        }
        final DownloadNotificationUtil downloadNotificationUtil = new DownloadNotificationUtil();
        downloadFile(context, url,
                savePath, fileName, progressDialog, isContinue, new DownloadListener() {
                    @Override
                    public void onStart() {
                        LogUtils.i("开始下载");
                        if (progressDialog != null)
                            progressDialog.setProgress(0);
                    }

                    @Override
                    public void onProgress(int currentLength) {
                        LogUtils.i("正在下载" + currentLength);
                        downloadModel.progress = currentLength;
                        downloadModel.downType = DownloadEnum.Downloading.getCode();
                        if (progressDialog != null)
                            progressDialog.setProgress(currentLength);
                        SharedPreferencesUtils.setDownloadUtilList(DOWNLOAD_FILE_SHARE_SAVE, downloadModelList);
                        downloadNotificationUtil.sendDefaultNotice(context, context.getString(R.string.file_download_ing) + (TextUtils.isEmpty(fileName) ? "" : fileName),
                                context.getString(R.string.file_download_progress) + "：" + currentLength + "%", currentLength
                                , NOTICE_DOWNLOAD_ID + downloadModel.id);
                    }

                    @Override
                    public void onFinish(String localPath) {
                        LogUtils.i("下载完成");
                        downloadModel.downType = DownloadEnum.DownloadFinish.getCode();
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.cancel();
                        }
                        downloadNotificationUtil.cancelNotification(context, NOTICE_DOWNLOAD_ID + downloadModel.id);
                        downloadModelList.remove(downloadModel);
                        SharedPreferencesUtils.setDownloadUtilList(DOWNLOAD_FILE_SHARE_SAVE, downloadModelList);
//                        ToastUtil.showMessage(context.getString(R.string.file_download_finish));
                    }

                    @Override
                    public void onFailure() {
                        LogUtils.i("下载失败");
                        downloadModel.downType = DownloadEnum.DownloadFail.getCode();
                        SharedPreferencesUtils.setDownloadUtilList(DOWNLOAD_FILE_SHARE_SAVE, downloadModelList);
                        downloadNotificationUtil.cancelNotification(context, NOTICE_DOWNLOAD_ID + downloadModel.id);
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.cancel();
                        }
                    }

                    @Override
                    public void onCancel() {
                        downloadModelList.remove(downloadModel);
                        SharedPreferencesUtils.setDownloadUtilList(DOWNLOAD_FILE_SHARE_SAVE, downloadModelList);
                        downloadModel.downType = DownloadEnum.DownloadCancel.getCode();
                        downloadNotificationUtil.cancelNotification(context, NOTICE_DOWNLOAD_ID + downloadModel.id);
                    }

                    @Override
                    public void onPause() {
                        SharedPreferencesUtils.setDownloadUtilList(DOWNLOAD_FILE_SHARE_SAVE, downloadModelList);
                        downloadModel.downType = DownloadEnum.DownloadPause.getCode();
                        downloadNotificationUtil.cancelNotification(context, NOTICE_DOWNLOAD_ID + downloadModel.id);
                    }
                });
    }

    public void downloadFile(final Context context, final String url, String savePath,final String fileName,final ProgressDialog progressDialog ,
                             boolean isContinue,final DownloadListener downloadListener) {
        PATH_CHALLENGE_FILE = TextUtils.isEmpty(savePath)?Environment.getExternalStorageDirectory() + SAVE_PATH_FOLDER_DEFAULT
                :Environment.getExternalStorageDirectory()+File.separator+savePath;

        //通过Url得到文件并创建本地文件
        if (FileUtils.createOrExistsDir(PATH_CHALLENGE_FILE)) {
            String name = url;
            if(TextUtils.isEmpty(fileName)){
                int i = name.lastIndexOf('/');//一定是找最后一个'/'出现的位置
                if (i != -1) {
                    name = name.substring(i);
                }
            }else{
                name = File.separator+fileName;
            }
            downloadModel.mFilePath = PATH_CHALLENGE_FILE +
                    name;
            downloadModel.fileName = name.substring(1,name.length());
        }
        if (TextUtils.isEmpty(downloadModel.mFilePath)) {
            Log.e(TAG, "downloadFile: 存储路径为空了");
            return;
        }
        //建立一个文件
        mFile = new File(downloadModel.mFilePath);
        //此处直接进行断点下载
        final long currentProgress = isContinue?(long)SharedPreferencesUtils.getParam(DOWNLOAD_FILE_SHARE_PRE+downloadModel.mFilePath,0l):0;
        if(currentProgress == 0) {
            downloadFile(url, downloadListener);
        }else{
            downloadFileContinue(url,currentProgress,downloadListener);
        }
        //屏蔽的代码为继续下载提示
//        if(!(!FileUtils.isFileExists(mFile) && FileUtils.createOrExistsFile(mFile)) ){
//            final long currentProgress = isContinue?(long)SharedPreferencesUtils.getParam(DOWNLOAD_FILE_SHARE_PRE+downloadModel.mFilePath,0l):0;
//            String text = "";
//            if(currentProgress!=0){
//                //存在下载记录
//                text = context.getString(R.string.file_download_exist_continue);
//            }else{
//                text = context.getString(R.string.file_download_exist_start);
//            }
//            DialogUtil.confirmDialog(context,text, new DialogUtil.FeedBack() {
//                @Override
//                public void onSure() {
//                    mFile.deleteOnExit();
//                    if(progressDialog != null) {
//                        progressDialog.show();
//                    }
//                    if(currentProgress == 0) {
//                        downloadFile(url, downloadListener);
//                    }else{
//                        downloadFileContinue(url,currentProgress,downloadListener);
//                    }
//                }
//
//                @Override
//                public void onNo() {
//                }
//            });
//        }else{
//            if(progressDialog != null)
//            progressDialog.show();
//            downloadFile(url,downloadListener);
//        }
    }


    /**
     * 断点续传
     * @param url
     * @param currentProgress 上次字节
     * @param downloadListener
     */
    private void downloadFileContinue(String url, long currentProgress,  final DownloadListener downloadListener){
        downloadFile(url,currentProgress, downloadListener);
    }
    private void downloadFile(String url,final DownloadListener downloadListener){
        if(downloadModel.id == 0) {
            downloadModelList.add(downloadModel);
            downloadModel.id = downloadModelList.size();
        }
        downloadFile(url,0,downloadListener);
    }
    private void downloadFile(String url,final long currentProgress,final DownloadListener downloadListener){
        if (mApi == null) {
            Log.e(TAG, "downloadFile: 下载接口为空了");
            return;
        }
        if(currentProgress == 0) {
            mCall = mApi.downloadFile(url);
        }else{
            HashMap<String, String> headers = new HashMap<>(2);
            headers.put("Range", "bytes="+currentProgress+"-");
            mCall = mApi.downloadFileContinue(headers,url);
        }
        mCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull final Response<ResponseBody> response) {
                //下载文件放在子线程
                mThread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        //保存到本地
                        writeFile2Disk(response,mFile, currentProgress, downloadListener);
                    }
                };
                mThread.start();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                downloadListener.onFailure(); //下载失败
            }
        });
    }
    //将下载的文件写入本地存储
    private void writeFile2Disk(Response<ResponseBody> response, File file,long currentProgress, DownloadListener downloadListener) {
        downloadListener.onStart();
        if(downloadListenerCustomer!=null)downloadListenerCustomer.onStart();
        long currentLength = currentProgress;
        OutputStream os = null;

        InputStream is = response.body().byteStream(); //获取下载输入流
        long totalLength = response.body().contentLength();
        totalLength = totalLength+currentProgress;
        downloadModel.size = totalLength;
        try {
            if(currentProgress == 0) {
                os = new FileOutputStream(file); //输出流
            }else{
                os = new FileOutputStream(file,true); //追写输出流
            }
            int len;
            byte[] buff = new byte[1024*10];
            while ((len = is.read(buff)) != -1) {
                if(downloadModel.downType ==  DownloadEnum.DownloadCancel.getCode()){//取消下载
                    file.deleteOnExit();
                    downloadListener.onCancel();
                    if(downloadListenerCustomer!=null)downloadListenerCustomer.onCancel();
                    break;
                }else if(downloadModel.downType ==  DownloadEnum.DownloadPause.getCode()){//暂停下载
                    downloadListener.onPause();
                    if(downloadListenerCustomer!=null)downloadListenerCustomer.onPause();
                    break;
                }
                os.write(buff, 0, len);
                currentLength += len;
                Log.i(TAG, "当前进度: " + currentLength);
                //保存下载进度，为断点续传处理
                SharedPreferencesUtils.setParam(DOWNLOAD_FILE_SHARE_PRE+file.getPath(),currentLength);
                //计算当前下载百分比，并经由回调传出
                downloadListener.onProgress((int) (100 * currentLength / totalLength));
                if(downloadListenerCustomer!=null)downloadListenerCustomer.onProgress((int) (100 * currentLength / totalLength));
                //当百分比为100时下载结束，调用结束回调，并传出下载后的本地路径
                if ((int) (100 * currentLength / totalLength) >= 100) {
                    downloadListener.onFinish(downloadModel.mFilePath); //下载完成
                    if(downloadListenerCustomer!=null)downloadListenerCustomer.onFinish(downloadModel.mFilePath); //下载完成
                    SharedPreferencesUtils.setParam(DOWNLOAD_FILE_SHARE_PRE+file.getPath(),0l);
                }
            }
        }catch (Exception e) {
            downloadListener.onFailure();
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close(); //关闭输出流
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close(); //关闭输入流
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
