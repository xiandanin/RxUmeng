package com.dyhdyh.rxumeng.example;

import android.app.Application;
import android.content.Context;

import com.dyhdyh.rxumeng.social.RxUmengSocial;
import com.dyhdyh.rxumeng.social.UmengSocialRegisterCallback;
import com.dyhdyh.rxumeng.social.UmengSocialResumeCallback;
import com.dyhdyh.widget.loadingbar2.LoadingBar;
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

        //基础组件初始化 https://developer.umeng.com/docs/66632/detail/66890#h2-u521Du59CBu53166
        String umengAppKey = BuildConfig.EXAMPLE_UMENG_KEY;
        UMConfigure.init(getApplicationContext(), umengAppKey, "Test", UMConfigure.DEVICE_TYPE_PHONE, "");
        UMConfigure.setLogEnabled(BuildConfig.DEBUG);

        //社会化sdk初始化
        RxUmengSocial.get().register(this, new UmengSocialRegisterCallback() {
            @Override
            public void onRegister() {
                //设置平台appid
                PlatformConfig.setWeixin(BuildConfig.EXAMPLE_WECHAT_APPID, BuildConfig.EXAMPLE_WECHAT_APIKEY);
                PlatformConfig.setQQZone(BuildConfig.EXAMPLE_QQ_APPID, BuildConfig.EXAMPLE_QQ_APPKEY);
                PlatformConfig.setSinaWeibo(BuildConfig.EXAMPLE_WEIBO_APPKEY, BuildConfig.EXAMPLE_WEIBO_APPSECRET, "http://sns.whalecloud.com/sina2/callback");
            }
        }, new UmengSocialResumeCallback() {
            @Override
            public void onResume(Context context) {
                //调用分享的Activity的onResume生命周期回调
                //Demo中是统一在这里处理了Loading的关闭
                LoadingBar.dialog(context).cancel();
            }
        });
    }
}
