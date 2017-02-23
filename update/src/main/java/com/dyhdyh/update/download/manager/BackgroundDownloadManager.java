package com.dyhdyh.update.download.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.TextUtils;

import com.dyhdyh.update.download.DownloadService;
import com.dyhdyh.update.download.UpdateAgent;
import com.dyhdyh.update.download.listener.OnDownloadListener;
import com.dyhdyh.update.download.listener.OnUpdateDownloadListener;
import com.dyhdyh.update.download.utils.SimpleLog;

import java.io.File;

/**
 * 下载器
 * author  dengyuhan
 * created 2017/2/22 16:52
 */
@Deprecated
public class BackgroundDownloadManager {
    private Context mContext;
    private DownloadService mDownloadService;

    //使用ServiceConnection来监听Service状态的变化
    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mDownloadService = null;
            SimpleLog.d(this, "onServiceDisconnected-----" + name);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mDownloadService = ((DownloadService.DownloadBinder) binder).getService();
            if (mDownloadService.getDownloadListener()==null){
                mDownloadService.setDownloadListener(new OnUpdateDownloadListener(mContext));
            }
            mDownloadService.setOkHttpClient(UpdateAgent.getUpdateConfig().getOkHttpClient());
            SimpleLog.d(this, "onServiceConnected-----" + name);

        }
    };

    public BackgroundDownloadManager(Context context) {
        this.mContext = context;
    }

    public void bindDownloadService() {
        Intent intent = new Intent(mContext, DownloadService.class);
        mContext.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    public void unbindService() {
        mContext.unbindService(conn);
    }


    public void start(String downloadUrl, File outFile) {
        SimpleLog.d(this, "开始下载-----" + downloadUrl);
        mDownloadService.startDownload(downloadUrl, outFile);
    }


    public void start(String downloadUrl) {
        String apkFileName=downloadUrl.substring(downloadUrl.lastIndexOf("/"),downloadUrl.length());
        String defaultApkFileName=System.currentTimeMillis()+".apk";
        start(downloadUrl,new File(UpdateAgent.getUpdateConfig().getDownloadDirectory(), TextUtils.isEmpty(apkFileName)?defaultApkFileName:apkFileName));
    }

    public void stop() {
        SimpleLog.d(this, "停止下载");
        mDownloadService.stopDownload();
        unbindService();
    }

    public BackgroundDownloadManager setDownloadListener(OnDownloadListener mDownloadListener) {
        mDownloadService.setDownloadListener(mDownloadListener);
        return this;
    }

}
