package com.dyhdyh.update.download.config;

import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.text.TextUtils;

import com.dyhdyh.update.download.R;


/**
 * author  dengyuhan
 * created 2017/2/22 15:18
 */
public class NotificationConfig {

    private String contentTitle;
    private String contentText;
    private String contentInfo;
    private boolean showProgress;
    /**点击通知后的操作*/
    private PendingIntent contentIntent;
    /**删除通知后的操作*/
    private PendingIntent deleteIntent;
    private int smallIcon;
    private int max;
    private int progress;
    private int flags;

    private NotificationConfig(Builder builder) {
        this.contentTitle = builder.contentTitle;
        this.contentText = builder.contentText;
        this.smallIcon = builder.smallIcon;
        this.max = builder.max;
        this.progress = builder.progress;
        this.showProgress = builder.showProgress;
        this.contentIntent = builder.contentIntent;
        this.deleteIntent = builder.deleteIntent;
        if (this.showProgress){
            if (TextUtils.isEmpty(builder.contentInfo)){
                builder.setContentInfo(String.format("%d%%",progress));
            }
        }
        this.contentInfo = builder.contentInfo;
        this.flags = builder.flags;
    }

    public PendingIntent getContentIntent() {
        return contentIntent;
    }

    public PendingIntent getDeleteIntent() {
        return deleteIntent;
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public String getContentText() {
        return contentText;
    }

    public String getContentInfo() {
        return contentInfo;
    }

    public int getSmallIcon() {
        return smallIcon;
    }

    public int getMax() {
        return max;
    }

    public int getProgress() {
        return progress;
    }

    public int getFlags() {
        return flags;
    }

    public static class Builder{
        private String contentTitle;
        private String contentText;
        private String contentInfo;
        private boolean showProgress;
        private PendingIntent contentIntent;
        private PendingIntent deleteIntent;
        private int smallIcon;
        private int max;
        private int progress;
        private int flags;

        public Builder setContentIntent(PendingIntent contentIntent) {
            this.contentIntent = contentIntent;
            return this;
        }

        public Builder setDeleteIntent(PendingIntent deleteIntent) {
            this.deleteIntent = deleteIntent;
            return this;
        }

        public Builder setFlags(int flags) {
            this.flags = flags;
            return this;
        }

        public Builder setContentTitle(String contentTitle) {
            this.contentTitle = contentTitle;
            return this;
        }

        public Builder setContentText(String contentText) {
            this.contentText = contentText;
            return this;
        }


        public Builder setContentInfo(String contentInfo) {
            this.contentInfo = contentInfo;
            return this;
        }

        public Builder setMax(int max) {
            this.max = max;
            return this;
        }


        public Builder setProgress(int progress) {
            this.progress = progress;
            return this;
        }

        public Builder setSmallIcon(int smallIcon) {
            this.smallIcon = smallIcon;
            return this;
        }


        public Builder setShowProgress(boolean showProgress) {
            this.showProgress = showProgress;
            return this;
        }

        public NotificationConfig builder() {
            return new NotificationConfig(this);
        }
    }


    public static NotificationConfig.Builder defaultConfig(Context context) {
        //应用名称
        String appName = null;
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getPackageInfo(context.getPackageName(),0).applicationInfo;
            appName=applicationInfo.loadLabel(packageManager).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //图标
        Resources res = context.getResources();
        int drawableId = res.getIdentifier("ic_launcher", "drawable", context.getPackageName());
        if (drawableId==0){
            drawableId = res.getIdentifier("ic_launcher", "mipmap", context.getPackageName());
        }
        String contentTile=String.format(context.getString(R.string.default_download_title),appName);
        return new NotificationConfig.Builder().setContentTitle(contentTile)
                .setSmallIcon(drawableId)
                .setContentTitle(contentTile);

    }
}
