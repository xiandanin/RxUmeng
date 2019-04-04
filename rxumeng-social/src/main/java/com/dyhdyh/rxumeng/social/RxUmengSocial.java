package com.dyhdyh.rxumeng.social;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.dyhdyh.rxumeng.social.exception.UmengPlatformInstallException;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author dengyuhan
 * created 2018/7/11 15:19
 */
public class RxUmengSocial {

    private UmengSocialResumeCallback mCallback;
    private SHARE_MEDIA mShareMedia;
    //是否需要检查平台可用性
    private boolean mCheckPlatform = true;

    private static RxUmengSocial mInstance;

    private RxUmengSocial() {
    }

    public static RxUmengSocial get() {
        synchronized (RxUmengSocial.class) {
            if (mInstance == null) {
                mInstance = new RxUmengSocial();
            }
            mInstance.mShareMedia = null;
            mInstance.mCheckPlatform = true;
        }
        return mInstance;
    }

    public void register(Context context, UmengSocialRegisterCallback registerCallback, UmengSocialResumeCallback callback) {
        UMShareConfig config = new UMShareConfig();
        config.isNeedAuthOnGetUserInfo(true);
        this.register(context, config, registerCallback, callback);
    }

    public void register(Context context, UMShareConfig config, UmengSocialRegisterCallback registerCallback, UmengSocialResumeCallback resumeCallback) {
        if (registerCallback != null) {
            registerCallback.onRegister();
        }
        UMShareAPI.get(context.getApplicationContext()).setShareConfig(config);
        this.mCallback = resumeCallback;
    }

    public RxUmengSocial setShareMedia(SHARE_MEDIA shareMedia) {
        this.mShareMedia = shareMedia;
        return this;
    }

    /**
     * 是否需要检查平台可用性
     *
     * @param checkPlatform 默认true
     */
    public RxUmengSocial setCheckPlatform(boolean checkPlatform) {
        this.mCheckPlatform = checkPlatform;
        return this;
    }

    public Observable<UmengAuthResult> getPlatformInfo(final Activity activity) {
        final UMShareAPI shareAPI = UMShareAPI.get(activity.getApplicationContext());
        return Observable.create(new ObservableOnSubscribe<UmengAuthResult>() {
            @Override
            public void subscribe(ObservableEmitter<UmengAuthResult> emitter) throws Exception {
                if (mCheckPlatform && !isPlatformAvailable(activity, mShareMedia)) {
                    emitter.onError(new UmengPlatformInstallException(mShareMedia));
                } else {
                    shareAPI.getPlatformInfo(activity, mShareMedia,
                            new UmengAuthEmitterListener(emitter));
                }
            }
        }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<UmengAuthResult> deleteOauth(final Activity activity) {
        final UMShareAPI shareAPI = UMShareAPI.get(activity.getApplicationContext());
        return Observable.create(new ObservableOnSubscribe<UmengAuthResult>() {
            @Override
            public void subscribe(ObservableEmitter<UmengAuthResult> emitter) throws Exception {
                shareAPI.deleteOauth(activity, mShareMedia,
                        new UmengAuthEmitterListener(emitter));
            }
        }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SHARE_MEDIA> shareUrl(Activity activity, final String title, final String message, final UMImage image, final String url) {
        return createShareAction(activity, new Function<ShareAction, ShareAction>() {
            @Override
            public ShareAction apply(ShareAction shareAction) throws Exception {
                UMWeb web = new UMWeb(url);
                web.setThumb(image);
                web.setTitle(title);
                web.setDescription(message);
                shareAction.withMedia(web);
                return shareAction;
            }
        });
    }

    public Observable<SHARE_MEDIA> shareImage(final Activity activity, UMImage image) {
        return Observable.just(image).map(new Function<UMImage, UMImage>() {
            @Override
            public UMImage apply(UMImage source) throws Exception {
                if (source.getThumbImage() == null) {
                    //如果没有缩略图 就生成一个缩略图
                    final Bitmap bitmap = source.asBitmap();
                    int width = 300;
                    int height = (int) ((float) width / bitmap.getWidth() * bitmap.getHeight());
                    Bitmap thumb = Bitmap.createScaledBitmap(bitmap, width, height, true);
                    source.setThumb(new UMImage(activity.getApplicationContext(), thumb));
                }
                return source;
            }
        }).subscribeOn(Schedulers.io())
                .flatMap(new Function<UMImage, ObservableSource<SHARE_MEDIA>>() {
                    @Override
                    public ObservableSource<SHARE_MEDIA> apply(final UMImage newImage) throws Exception {
                        return createShareAction(activity, new Function<ShareAction, ShareAction>() {
                            @Override
                            public ShareAction apply(ShareAction shareAction) throws Exception {
                                shareAction.withMedia(newImage);
                                return shareAction;
                            }
                        });
                    }
                });
    }

    /**
     * 创建分享
     *
     * @param activity
     * @param function
     * @return
     */
    private Observable<SHARE_MEDIA> createShareAction(final Activity activity, final Function<ShareAction, ShareAction> function) {
        return Observable.create(new ObservableOnSubscribe<SHARE_MEDIA>() {
            @Override
            public void subscribe(ObservableEmitter<SHARE_MEDIA> emitter) throws Exception {
                if (mCheckPlatform && !isPlatformAvailable(activity, mShareMedia)) {
                    emitter.onError(new UmengPlatformInstallException(mShareMedia));
                } else {
                    ShareAction action = function.apply(
                            new ShareAction(activity)
                                    .setPlatform(mShareMedia)
                                    .setCallback(new UmengShareEmitterListener(emitter))
                    );
                    action.share();
                }
            }
        }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /*--------Activity 要重写的方法----------*/

    public void onResume(Context context) {
        if (mCallback != null) {
            mCallback.onResume(context);
        }
    }

    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        UMShareAPI.get(context.getApplicationContext()).onActivityResult(requestCode, resultCode, data);
    }

    public void onDestroy(Context context) {
        UMShareAPI.get(context.getApplicationContext()).release();
    }

    /*--------Activity 要重写的方法----------*/


    /**
     * 是否有权限(没有权限会请求申请)
     *
     * @param activity
     * @return
     */
    public boolean hasPermissions(Activity activity) {
        if (!isSocializePermissions(activity)) {
            requestPermissions(activity);
            return false;
        }
        return true;
    }


    /**
     * 请求权限
     *
     * @param activity
     */
    private void requestPermissions(Activity activity) {
        String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_LOGS, Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SET_DEBUG_APP,
                Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.WRITE_APN_SETTINGS};
        ActivityCompat.requestPermissions(activity, mPermissionList, 123);
    }

    /**
     * 是否有权限
     *
     * @param activity
     * @return
     */
    private boolean isSocializePermissions(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_LOGS) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.SET_DEBUG_APP) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_APN_SETTINGS) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * 是否可用
     *
     * @param activity
     * @param share_media
     * @return
     */
    public boolean isPlatformAvailable(Activity activity, SHARE_MEDIA share_media) {
        if (SHARE_MEDIA.SINA == share_media) {
            //新浪可以用网页 直接true
            return true;
        } else if (SHARE_MEDIA.QQ == share_media || SHARE_MEDIA.QZONE == share_media) {
            //QQ
            return UmengPlatformInfo.isInstall(activity, UmengPlatformInfo.PackageNames.QQ);
        } else {
            return UMShareAPI.get(activity).isInstall(activity, share_media);
        }
    }

}
