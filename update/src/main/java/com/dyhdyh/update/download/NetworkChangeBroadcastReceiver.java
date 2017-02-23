package com.dyhdyh.update.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.dyhdyh.update.download.listener.NetworkChangeListener;

/**
 * 网络状态监听
 * author  dengyuhan
 * created 2017/2/22 16:53
 */
public class NetworkChangeBroadcastReceiver extends BroadcastReceiver {
    private NetworkChangeListener mNetworkChangeListener;

    public void setNetworkChangeListener(NetworkChangeListener networkChangeListener) {
        this.mNetworkChangeListener = networkChangeListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mNetworkChangeListener==null){
            return;
        }
        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
            //网络连接
            if (networkInfo != null && networkInfo.isAvailable()) {
                mNetworkChangeListener.onAvailable(networkInfo.getType());
            } else {
                //网络断开
                mNetworkChangeListener.onUnavailable();
            }
        }

    }

}