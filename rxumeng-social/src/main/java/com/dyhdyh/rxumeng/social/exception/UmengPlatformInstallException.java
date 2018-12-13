package com.dyhdyh.rxumeng.social.exception;

import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * @author dengyuhan
 * created 2018/7/11 17:22
 */
public class UmengPlatformInstallException extends Exception {
    private SHARE_MEDIA shareMedia;

    public UmengPlatformInstallException(SHARE_MEDIA shareMedia) {
        super(String.format("%s Not installed", shareMedia.name()));
        this.shareMedia = shareMedia;
    }

    public SHARE_MEDIA getShareMedia() {
        return shareMedia;
    }
}
