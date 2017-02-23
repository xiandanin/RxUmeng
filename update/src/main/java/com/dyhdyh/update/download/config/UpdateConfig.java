package com.dyhdyh.update.download.config;

import android.content.Context;
import android.os.Environment;

import com.dyhdyh.update.download.listener.DownloadOnlyWifiChangeListener;
import com.dyhdyh.update.download.manager.NetworkChangeManager;

import java.io.File;
import java.io.Serializable;

import okhttp3.OkHttpClient;

/**
 * 更新的配置
 * author  dengyuhan
 * created 2017/2/22 13:32
 */
public class UpdateConfig implements Serializable{

    private Context mContext;

    private boolean mDebug;
    /**只在wifi情况下下载*/
    private boolean mUpdateOnlyWifi;
    /**是否通知栏进度*/
    private boolean mProgressNotification;
    /**是否通知下载完成*/
    private boolean mCompleteNotification;
    /**是否静默下载*/
    private boolean mSilentDownload;
    /**是否下载完成自动安装*/
    private boolean mAutoInstall;
    /**存放的文件夹*/
    private File mDownloadDirectory;
    /**网络客户端*/
    private OkHttpClient mOkHttpClient;

    private UpdateConfig(Context context,Builder builder) {
        this.mContext = context;
        this.mDebug = builder.mDebug;
        this.mUpdateOnlyWifi = builder.mUpdateOnlyWifi;
        //如果设置只有wifi才能下载,就注册网络监听
        if (this.mUpdateOnlyWifi) {
            NetworkChangeManager.register(mContext, new DownloadOnlyWifiChangeListener());
        } else {
            NetworkChangeManager.unregisterReceiver(mContext);
        }
        this.mProgressNotification = builder.mProgressNotification;
        this.mCompleteNotification = builder.mCompleteNotification;
        this.mSilentDownload = builder.mSilentDownload;
        this.mDownloadDirectory = builder.mDownloadDirectory!=null&&builder.mDownloadDirectory.exists()?builder.mDownloadDirectory: Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        this.mAutoInstall = builder.mAutoInstall;
        this.mOkHttpClient = builder.mOkHttpClient;
    }


    public static UpdateConfig defaultConfig(Context context) {
        return new UpdateConfig.Builder()
                .setDebug(true)
                .setUpdateOnlyWifi(false)
                .setCompleteNotification(true)
                .setProgressNotification(true)
                .setAutoInstall(true)
                .setOkHttpClient(new OkHttpClient.Builder().build()).build(context);
    }

    public UpdateConfig.Builder newBuilder() {
        return new Builder(this);

    }

    public boolean isUpdateOnlyWifi() {
        return mUpdateOnlyWifi;
    }

    public boolean isCompleteNotification() {
        return mCompleteNotification;
    }

    public boolean isProgressNotification() {
        return mProgressNotification;
    }

    public File getDownloadDirectory() {
        return mDownloadDirectory;
    }

    public boolean isAutoInstall() {
        return mAutoInstall;
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }


    public boolean isDebug() {
        return mDebug;
    }

    public boolean isSilentDownload() {
        return mSilentDownload;
    }

    public static class Builder{
        private boolean mDebug;
        private boolean mUpdateOnlyWifi;
        private boolean mSilentDownload;
        private boolean mProgressNotification;
        private boolean mCompleteNotification;
        private File mDownloadDirectory;
        private boolean mAutoInstall;
        private OkHttpClient mOkHttpClient;

        private Builder(UpdateConfig config){
            this.mDebug=config.mDebug;
            this.mUpdateOnlyWifi=config.mUpdateOnlyWifi;
            this.mSilentDownload=config.mSilentDownload;
            this.mProgressNotification=config.mProgressNotification;
            this.mCompleteNotification=config.mCompleteNotification;
            this.mDownloadDirectory=config.mDownloadDirectory;
            this.mAutoInstall = config.mAutoInstall;
            this.mOkHttpClient=config.mOkHttpClient;
        }

        public Builder(){

        }

        public Builder setSilentDownload(boolean silentDownload) {
            this.mSilentDownload = silentDownload;
            return this;
        }

        public Builder setDebug(boolean debug) {
            this.mDebug = debug;
            return this;
        }


        public Builder setOkHttpClient(OkHttpClient okHttpClient) {
            this.mOkHttpClient = okHttpClient;
            return this;
        }

        public Builder setProgressNotification(boolean progressNotification) {
            this.mProgressNotification = progressNotification;
            return this;
        }

        public Builder setCompleteNotification(boolean completeNotification) {
            this.mCompleteNotification = completeNotification;
            return this;
        }

        public Builder setUpdateOnlyWifi(boolean updateOnlyWifi) {
            this.mUpdateOnlyWifi = updateOnlyWifi;
            return this;
        }

        public Builder setDownloadDirectory(File downloadDirectory) {
            this.mDownloadDirectory = downloadDirectory;
            return this;
        }

        public Builder setAutoInstall(boolean autoInstall) {
            this.mAutoInstall = autoInstall;
            return this;
        }

        public UpdateConfig build(Context context){
            return new UpdateConfig(context,this);
        }

    }

}
