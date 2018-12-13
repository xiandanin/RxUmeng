package com.dyhdyh.rxumeng.social;

import com.dyhdyh.rxumeng.social.exception.UmengPlatformCancelException;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

import io.reactivex.ObservableEmitter;

/**
 * @author dengyuhan
 * created 2018/7/11 15:52
 */
public class UmengAuthEmitterListener implements UMAuthListener {
    private ObservableEmitter<UmengAuthResult> mEmitter;

    public UmengAuthEmitterListener(ObservableEmitter<UmengAuthResult> emitter) {
        this.mEmitter = emitter;
    }

    @Override
    public void onStart(SHARE_MEDIA share_media) {

    }

    @Override
    public void onComplete(SHARE_MEDIA share_media, int code, Map<String, String> map) {
        mEmitter.onNext(new UmengAuthResult(share_media, code, map));
    }

    @Override
    public void onError(SHARE_MEDIA share_media, int code, Throwable throwable) {
        mEmitter.onError(throwable);
    }

    @Override
    public void onCancel(SHARE_MEDIA share_media, int code) {
        mEmitter.onError(new UmengPlatformCancelException(share_media));
    }
}
