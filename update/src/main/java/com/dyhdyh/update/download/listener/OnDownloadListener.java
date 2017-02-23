package com.dyhdyh.update.download.listener;

import java.io.File;

/**
 * author  dengyuhan
 * created 2017/2/22 13:37
 */
public interface OnDownloadListener {
    void OnDownloadStart();

    void OnDownloadUpdate(long progress,long length);

    void OnDownloadError(Throwable e);

    void OnDownloadEnd(long length, File file);

    void OnDownloadStop();
}
