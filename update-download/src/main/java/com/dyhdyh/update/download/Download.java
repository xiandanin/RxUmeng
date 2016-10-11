package com.dyhdyh.update.download;

import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

/**
 * 下载器
 * @author dengyuhan
 */
public class Download {
    private static long downloadId;

    /**
     * 启动下载
     */
    public static void start(Context context, String downloadUrl, String fileName) {
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!file.exists()){
            file.mkdirs();
        }
        //
        if (downloadId!=0){
            return;
        }
        DownloadManager downloadManager= (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        request.setMimeType("application/vnd.android.package-archive");
        int appNameId=context.getResources().getIdentifier("app_name","string",context.getPackageName());
        if (appNameId>0){
            request.setTitle(context.getString(appNameId));
        }
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(true);
        request.allowScanningByMediaScanner();
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName);
        downloadId=downloadManager.enqueue(request);

        registerDownloadBroadcastReceiver(context);
    }

    /**
     * 注册下载完成的广播
     */
    public static void registerDownloadBroadcastReceiver(Context context) {
        IntentFilter filter=new IntentFilter();
        filter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        DownloadBroadcastReceiver downloadBroadcastReceiver = new DownloadBroadcastReceiver();
        context.registerReceiver(downloadBroadcastReceiver,filter);
    }

    public static long getDownloadId() {
        return downloadId;
    }

    public static void setDownloadId(long downloadId) {
        Download.downloadId = downloadId;
    }
}
