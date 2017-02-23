package com.dyhdyh.update.download.listener;

/**
 * author  dengyuhan
 * created 2017/2/22 17:03
 */
public interface NetworkChangeListener {

    /**
     * 网络连接
     * ConnectivityManager.TYPE_WIFI = WiFi网络
     * ConnectivityManager.TYPE_ETHERNET = 有线网络
     * ConnectivityManager.TYPE_MOBILE = 移动网络
     * ConnectivityManager.TYPE_BLUETOOTH = 蓝牙网络
     *
     */
    void onAvailable(int type);

    /**
     * 网络断开
     */
    void onUnavailable();
}
