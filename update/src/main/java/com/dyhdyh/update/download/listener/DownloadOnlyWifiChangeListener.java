package com.dyhdyh.update.download.listener;

import android.net.ConnectivityManager;

import com.dyhdyh.update.download.UpdateAgent;
import com.dyhdyh.update.download.utils.SimpleLog;

/**
 * author  dengyuhan
 * created 2017/2/22 17:18
 */
public class DownloadOnlyWifiChangeListener implements NetworkChangeListener {

    @Override
    public void onAvailable(int type) {
        SimpleLog.d(this,"onAvailable-----"+type);
        //如果是wifi,可以下载
        if (type == ConnectivityManager.TYPE_WIFI) {

        } else {
            //立即停止下载
            UpdateAgent.getNotificationManager().cancelDownloadNotification();
            UpdateAgent.getDownloadManager().stop();
        }
    }

    @Override
    public void onUnavailable() {
        SimpleLog.d(this,"onUnavailable");
    }
}
