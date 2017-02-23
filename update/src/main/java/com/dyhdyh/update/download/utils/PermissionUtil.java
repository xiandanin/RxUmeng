package com.dyhdyh.update.download.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * author  dengyuhan
 * created 2017/2/23 11:28
 */
public class PermissionUtil {
    public static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE=1;

    public static void checkPermissionAndRequest(Activity activity){
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        }
    }

}
