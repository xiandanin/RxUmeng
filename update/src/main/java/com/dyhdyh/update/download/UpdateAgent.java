package com.dyhdyh.update.download;

import android.app.Activity;
import android.content.Context;

import com.dyhdyh.update.download.config.UpdateConfig;
import com.dyhdyh.update.download.listener.OnUpdateDownloadListener;
import com.dyhdyh.update.download.manager.DownloadManager;
import com.dyhdyh.update.download.manager.DownloadNotificationManager;
import com.dyhdyh.update.download.utils.PermissionUtil;

/**
 * author  dengyuhan
 * created 2017/2/22 17:14
 */
public class UpdateAgent {
    private Context mContext;

    private UpdateConfig mUpdateConfig;
    private DownloadManager mDownloadManager;
    private DownloadNotificationManager mNotificationManager;

    private static UpdateAgent mInstance;

    public static UpdateAgent getInstance() {
        return mInstance;
    }

    private UpdateAgent(Context context, UpdateConfig config) {
        this.mContext = context;
        this.mUpdateConfig = config;
        this.mDownloadManager = new DownloadManager(context);
        this.mDownloadManager.setOkHttpClient(this.mUpdateConfig.getOkHttpClient());
        this.mNotificationManager = new DownloadNotificationManager(context);
    }


    public static void init(Context context) {
        init(context, UpdateConfig.defaultConfig(context));
    }

    public static void init(Context context, UpdateConfig config) {
        mInstance = new UpdateAgent(context, config);
        //mInstance.mDownloadManager.bindDownloadService();
        PermissionUtil.checkPermissionAndRequest((Activity) context);
    }

    public void destroy() {
        //mDownloadManager.unbindService();
    }


    public static DownloadManager getDownloadManager() {
        return mInstance.mDownloadManager;
    }

    public static DownloadNotificationManager getNotificationManager() {
        return mInstance.mNotificationManager;
    }

    public static UpdateConfig getUpdateConfig() {
        return mInstance.mUpdateConfig;
    }

    public void setUpdateConfig(UpdateConfig updateConfig) {
        this.mUpdateConfig = updateConfig;
    }


    public void update(String downloadUrl) {
        mDownloadManager.start(downloadUrl);
    }

    /**
     * 静默更新
     */
    public void silentUpdate(String downloadUrl) {
        UpdateConfig config = mUpdateConfig.newBuilder()
                .setUpdateOnlyWifi(true)
                .setCompleteNotification(true)
                .setProgressNotification(false)
                .setAutoInstall(false).build(mContext);
        setUpdateConfig(config);
        mDownloadManager.setDownloadListener(new OnUpdateDownloadListener(mContext))
                .start(downloadUrl);
    }
}
