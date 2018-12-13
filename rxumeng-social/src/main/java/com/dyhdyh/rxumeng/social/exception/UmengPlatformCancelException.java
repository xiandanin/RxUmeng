package com.dyhdyh.rxumeng.social.exception;

import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * @author dengyuhan
 * created 2018/7/11 20:27
 */
public class UmengPlatformCancelException extends Exception{
    private SHARE_MEDIA shareMedia;

    public UmengPlatformCancelException(SHARE_MEDIA shareMedia) {
        super(String.format("%s cancel", shareMedia.name()));
        this.shareMedia = shareMedia;
    }

    public SHARE_MEDIA getShareMedia() {
        return shareMedia;
    }
}
