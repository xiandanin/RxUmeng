package com.dyhdyh.rxumeng.social;

import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * @author dengyuhan
 * created 2019/4/4 10:41
 */
public interface OnSystemSocialListener {

    void onError(SHARE_MEDIA share_media, Throwable throwable);
}
