package com.dyhdyh.update.download.listener;

import com.dyhdyh.update.download.utils.SimpleLog;

import java.io.File;

/**
 * author  dengyuhan
 * created 2017/2/22 13:37
 */
public class SimpleDownloadListener implements OnDownloadListener {

    @Override
    public void OnDownloadStart() {
        SimpleLog.d(this,"OnDownloadStart");
    }

    @Override
    public void OnDownloadUpdate(long progress, long length) {
        SimpleLog.d(this,"OnDownloadUpdate------"+progress+"-----"+length);
    }


    @Override
    public void OnDownloadError(Throwable e) {
        SimpleLog.d(this,"OnDownloadError------"+e);
    }

    @Override
    public void OnDownloadEnd(long length, File file) {
        SimpleLog.d(this,"OnDownloadEnd------"+length+"-----"+file);
    }

    @Override
    public void OnDownloadStop() {
        SimpleLog.d(this,"OnDownloadStop");
    }

}
