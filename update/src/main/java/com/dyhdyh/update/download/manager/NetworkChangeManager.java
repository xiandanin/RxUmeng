package com.dyhdyh.update.download.manager;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.dyhdyh.update.download.NetworkChangeBroadcastReceiver;
import com.dyhdyh.update.download.listener.NetworkChangeListener;

/**
 * author  dengyuhan
 * created 2017/2/22 17:12
 */
public class NetworkChangeManager {
    private static NetworkChangeBroadcastReceiver broadcastReceiver;

    public static void register(Context context, NetworkChangeListener networkChangeListener){
        if (broadcastReceiver!=null){
            unregisterReceiver(context);
        }
        broadcastReceiver=new NetworkChangeBroadcastReceiver();
        broadcastReceiver.setNetworkChangeListener(networkChangeListener);
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(broadcastReceiver, mFilter);
    }

    public static void unregisterReceiver(Context context){
        if (broadcastReceiver!=null){
            context.unregisterReceiver(broadcastReceiver);
            broadcastReceiver=null;
        }
    }
}
