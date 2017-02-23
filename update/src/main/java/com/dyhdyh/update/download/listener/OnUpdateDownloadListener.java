package com.dyhdyh.update.download.listener;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.dyhdyh.update.download.UpdateAgent;
import com.dyhdyh.update.download.utils.NetworkUtil;

import java.io.File;

/**
 * author  dengyuhan
 * created 2017/2/22 13:37
 */
public class OnUpdateDownloadListener extends SimpleDownloadListener {
    private Context mContext;

    public OnUpdateDownloadListener(Context context) {
        this.mContext = context;
    }

    @Override
    public void OnDownloadStart() {
        super.OnDownloadStart();
        if (UpdateAgent.getUpdateConfig().isUpdateOnlyWifi()){
            if (!NetworkUtil.isWifi(mContext)){
                UpdateAgent.getDownloadManager().stop();
            }
        }
    }

    @Override
    public void OnDownloadStop() {
        super.OnDownloadStop();
        if (UpdateAgent.getUpdateConfig().isProgressNotification()){
            UpdateAgent.getNotificationManager().cancelDownloadNotification();
        }
    }


    @Override
    public void OnDownloadError(Throwable e) {
        super.OnDownloadError(e);
        if (UpdateAgent.getUpdateConfig().isProgressNotification()){
            UpdateAgent.getNotificationManager().cancelDownloadNotification();
        }
    }

    @Override
    public void OnDownloadUpdate(long progress, long length) {
        super.OnDownloadUpdate(progress, length);
        if (UpdateAgent.getUpdateConfig().isProgressNotification()){
            int p=(int)(progress*1.0/length*100);
            UpdateAgent.getNotificationManager().notificationDownloadProgress(100,p);
        }
    }

    @Override
    public void OnDownloadEnd(long length, File file) {
        super.OnDownloadEnd(length, file);
        if (UpdateAgent.getUpdateConfig().isCompleteNotification()){
            UpdateAgent.getNotificationManager().notificationDownloadComplete(file);
            if (UpdateAgent.getUpdateConfig().isAutoInstall()){
                Intent installIntent = new Intent(Intent.ACTION_VIEW);
                installIntent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                mContext.startActivity(installIntent);
            }
        }
    }
}
