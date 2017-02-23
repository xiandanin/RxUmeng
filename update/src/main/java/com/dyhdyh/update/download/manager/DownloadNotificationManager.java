package com.dyhdyh.update.download.manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.dyhdyh.update.download.R;
import com.dyhdyh.update.download.config.NotificationConfig;

import java.io.File;

/**
 * author  dengyuhan
 * created 2017/2/22 14:45
 */
public class DownloadNotificationManager {

    private Context mContext;
    private final int mNotificationId=10000;

    public DownloadNotificationManager(Context context) {
        this.mContext = context;
    }

    public void notification(NotificationConfig config) {
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(mContext);
        //需要进度
        if (config.isShowProgress()) {
            builder.setProgress(config.getMax(), config.getProgress(), false);
        }
        builder.setOngoing(config.isShowProgress())
                .setSmallIcon(config.getSmallIcon())
                .setContentTitle(config.getContentTitle())
                .setContentInfo(config.getContentInfo())
                .setContentText(config.getContentText())
                .setContentIntent(config.getContentIntent())
                .setDeleteIntent(config.getDeleteIntent());
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        } else {
            notification = builder.getNotification();
        }
        if (config.getFlags() != 0) {
            notification.flags = config.getFlags();
        }
        notificationManager.notify(mNotificationId, notification);
    }


    public void cancelDownloadNotification() {
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(mNotificationId);
    }

    /**
     * 下载进度的通知
     *
     * @param max
     * @param progress
     */
    public void notificationDownloadProgress(int max, int progress) {
        notification(NotificationConfig.defaultConfig(mContext).setShowProgress(true).setMax(max).setProgress(progress).builder());
    }

    /**
     * 下载完成的通知
     */
    public void notificationDownloadComplete(File apkFile) {
        String content = mContext.getString(R.string.default_download_complete_text);
        String title = mContext.getString(R.string.default_download_complete_title);

        //点击安装PendingIntent
        Uri uri = Uri.fromFile(apkFile);
        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
        PendingIntent intent = PendingIntent.getActivity(mContext, 0, installIntent, 0);

        notification(NotificationConfig.defaultConfig(mContext)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(intent)
                .setFlags(Notification.FLAG_AUTO_CANCEL)
                .builder());
    }
}
