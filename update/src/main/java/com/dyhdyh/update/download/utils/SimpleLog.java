package com.dyhdyh.update.download.utils;

import android.util.Log;

/**
 * author  dengyuhan
 * created 2017/2/22 17:52
 */
public class SimpleLog {
    public static void d(Object obj,String message){
        Log.d(obj.getClass().getSimpleName(),message);
    }
}
