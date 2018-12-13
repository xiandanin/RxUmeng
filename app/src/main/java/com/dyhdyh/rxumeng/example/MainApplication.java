package com.dyhdyh.rxumeng.example;

import android.app.Application;
import android.content.Context;

import com.dyhdyh.rxumeng.social.RxUmengSocial;
import com.dyhdyh.rxumeng.social.UmengSocialRegisterCallback;
import com.dyhdyh.rxumeng.social.UmengSocialResumeCallback;
import com.dyhdyh.widget.loading.dialog.LoadingDialog;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

/**
 * @author dengyuhan
 * created 2018/12/13 11:13
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //基础组件初始化
        String umengAppKey = BuildConfig.EXAMPLE_UMENG_KEY;
        UMConfigure.init(getApplicationContext(), umengAppKey, "Test", UMConfigure.DEVICE_TYPE_PHONE, "");
        UMConfigure.setLogEnabled(BuildConfig.DEBUG);

        //社会化sdk初始化
        RxUmengSocial.get().register(this, new UmengSocialRegisterCallback() {
            @Override
            public void onRegister() {
                PlatformConfig.setWeixin(BuildConfig.EXAMPLE_WECHAT_APPID, BuildConfig.EXAMPLE_WECHAT_APIKEY);
                PlatformConfig.setQQZone(BuildConfig.EXAMPLE_QQ_APPID, BuildConfig.EXAMPLE_QQ_APPKEY);
                PlatformConfig.setSinaWeibo(BuildConfig.EXAMPLE_WEIBO_APPKEY, BuildConfig.EXAMPLE_WEIBO_APPSECRET, "http://sns.whalecloud.com/sina2/callback");
            }
        }, new UmengSocialResumeCallback() {
            @Override
            public void onResume(Context context) {
                LoadingDialog.cancel();
            }
        });
    }
}
