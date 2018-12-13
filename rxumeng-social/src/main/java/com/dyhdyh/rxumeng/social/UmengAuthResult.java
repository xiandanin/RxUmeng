package com.dyhdyh.rxumeng.social;

import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

/**
 * @author dengyuhan
 * created 2018/7/11 15:53
 */
public class UmengAuthResult {
    private SHARE_MEDIA shareMedia;
    private int code;
    private Map<String, String> data;

    public UmengAuthResult() {
    }

    public UmengAuthResult(SHARE_MEDIA shareMedia, int code) {
        this.shareMedia = shareMedia;
        this.code = code;
    }

    public UmengAuthResult(SHARE_MEDIA shareMedia, int code, Map<String, String> data) {
        this.shareMedia = shareMedia;
        this.code = code;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public SHARE_MEDIA getShareMedia() {
        return shareMedia;
    }

    public void setShareMedia(SHARE_MEDIA shareMedia) {
        this.shareMedia = shareMedia;
    }
}
