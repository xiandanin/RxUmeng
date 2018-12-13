package com.dyhdyh.rxumeng.social;

import com.dyhdyh.rxumeng.social.exception.UmengPlatformCancelException;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import io.reactivex.ObservableEmitter;

/**
 * 分享监听转rxjava
 * @author dengyuhan
 * created 2018/7/11 15:52
 */
public class UmengShareEmitterListener implements UMShareListener {
    private ObservableEmitter<SHARE_MEDIA> mEmitter;

    public UmengShareEmitterListener(ObservableEmitter<SHARE_MEDIA> emitter) {
        this.mEmitter = emitter;
    }

    @Override
    public void onStart(SHARE_MEDIA share_media) {

    }

    @Override
    public void onResult(SHARE_MEDIA share_media) {
        mEmitter.onNext(share_media);
    }

    @Override
    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
        mEmitter.onError(throwable);
    }

    @Override
    public void onCancel(SHARE_MEDIA share_media) {
        mEmitter.onError(new UmengPlatformCancelException(share_media));
    }
}
