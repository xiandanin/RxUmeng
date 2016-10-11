package com.dyhdyh.update.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * 下载完成的广播
 *
 * @author dengyuhan
 */
public class DownloadBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            if (intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
                //点击
                downloadManager.remove(Download.getDownloadId());
            } else if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                //下载完成
                Uri downloadedFile = downloadManager.getUriForDownloadedFile(Download.getDownloadId());
                if (downloadedFile!=null){
                    install(context, downloadedFile.getPath());
                }
            }
            Download.setDownloadId(0);
            context.unregisterReceiver(this);
        }
    }

    private void install(Context context, String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

}
