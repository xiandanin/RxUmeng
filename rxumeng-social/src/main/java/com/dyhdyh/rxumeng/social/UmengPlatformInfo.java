package com.dyhdyh.rxumeng.social;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.umeng.socialize.bean.SHARE_MEDIA;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dengyuhan
 * created 2019/4/3 18:12
 */
public class UmengPlatformInfo {

    //没有成功事件的平台
    private static List<SHARE_MEDIA> NOT_SUCCESS_PLATFORM;
    //包名集合
    private static Map<SHARE_MEDIA, String> PACKAGE_NAME_MAPPING;
    //分享activity集合
    private static Map<SHARE_MEDIA, String> RESOLVE_NAME_MAPPING;

    static {
        //6月份新版微信客户端发布后，不再返回用户是否分享完成事件，即原先的cancel事件和success事件将统一为success事件。
        NOT_SUCCESS_PLATFORM = new ArrayList<>();
        NOT_SUCCESS_PLATFORM.add(SHARE_MEDIA.WEIXIN);
        NOT_SUCCESS_PLATFORM.add(SHARE_MEDIA.WEIXIN_CIRCLE);
        NOT_SUCCESS_PLATFORM.add(SHARE_MEDIA.INSTAGRAM);
        NOT_SUCCESS_PLATFORM.add(SHARE_MEDIA.FACEBOOK);
        NOT_SUCCESS_PLATFORM.add(SHARE_MEDIA.MORE);

        //包名
        PACKAGE_NAME_MAPPING = new HashMap<>();
        final Class<PackageNames> packageCls = PackageNames.class;
        final SHARE_MEDIA[] values = SHARE_MEDIA.values();
        for (SHARE_MEDIA shareMedia : values) {
            try {
                final Field field = packageCls.getField(shareMedia.name());
                final String value = (String) field.get(null);
                if (!TextUtils.isEmpty(value)) {
                    PACKAGE_NAME_MAPPING.put(shareMedia, value);
                }
            }catch (NoSuchFieldException ignored) {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //resolve集合
        RESOLVE_NAME_MAPPING = new HashMap<>();
        final Class<ResolveNames> resolveCls = ResolveNames.class;
        for (SHARE_MEDIA shareMedia : values) {
            try {
                final Field field = resolveCls.getField(shareMedia.name());
                final String value = (String) field.get(null);
                if (!TextUtils.isEmpty(value)) {
                    RESOLVE_NAME_MAPPING.put(shareMedia, value);
                }
            } catch (NoSuchFieldException ignored) {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 平台是否有分享成功事件
     *
     * @param shareMedia
     * @return
     */
    public static boolean isHasShareSuccessEvent(SHARE_MEDIA shareMedia) {
        return shareMedia != null && !NOT_SUCCESS_PLATFORM.contains(shareMedia);
    }

    /**
     * 根据SHARE_MEDIA获取resolve名
     *
     * @param shareMedia
     * @return
     */
    public static String getResolveName(SHARE_MEDIA shareMedia) {
        return RESOLVE_NAME_MAPPING.get(shareMedia);
    }

    /**
     * 根据SHARE_MEDIA获取包名
     *
     * @param shareMedia
     * @return
     */
    public static String getPackageName(SHARE_MEDIA shareMedia) {
        return PACKAGE_NAME_MAPPING.get(shareMedia);
    }


    /**
     * 获取包名集合
     *
     * @return
     */
    @NonNull
    public static List<String> getPackageNames() {
        return new ArrayList<>(PACKAGE_NAME_MAPPING.values());
    }

    /**
     * 根据SHARE_MEDIA集合获取包名集合
     *
     * @param shareMedias
     * @return
     */
    @NonNull
    public static List<String> getPackageNames(SHARE_MEDIA... shareMedias) {
        List<String> packageNames = new ArrayList<>();
        for (SHARE_MEDIA shareMedia : shareMedias) {
            final String packageName = PACKAGE_NAME_MAPPING.get(shareMedia);
            if (!TextUtils.isEmpty(packageName)) {
                packageNames.add(packageName);
            }
        }
        return packageNames;
    }

    public static boolean isInstall(Context context, SHARE_MEDIA shareMedia) {
        return isInstall(context, getPackageName(shareMedia));
    }

    public static boolean isInstall(Context context, String packageName) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            return packageInfo != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public interface PackageNames {
        //微信
        String WEIXIN = "com.tencent.mm";
        String WEIXIN_CIRCLE = WEIXIN;
        String WEIXIN_FAVORITE = WEIXIN;
        //QQ
        String QQ = "com.tencent.mobileqq";
        //QQ轻聊版
        String QQ_LITE = "com.tencent.qqlite";
        //QQ国际版
        String QQ_INT = "com.tencent.mobileqqi";
        //QQ hd mini
        String QQ_HD_MINI = "com.tencent.minihd.qq";
        //QQ pad
        String QQ_PAD = "com.tencent.android.pad";
        //QQ空间
        String QZONE = "com.qzone";
        //微博
        String SINA = "com.sina.weibo";
        //钉钉
        String DINGTALK = "com.alibaba.android.rimet";
        //支付宝
        String ALIPAY = "com.eg.android.AlipayGphone";
        //人人
        String RENREN = "com.renren.mobile.android";
        //豆瓣
        String DOUBAN = "com.douban.frodo";
        //有道云笔记
        String YNOTE = "com.youdao.note";
        //印象笔记
        String EVERNOTE = "com.yinxiang";
        //点点虫
        String LAIWANG = "com.alibaba.android.babylon";
        String LAIWANG_DYNAMIC = LAIWANG;
        //领英
        String LINKEDIN = "com.linkedin.android";
        //易信
        String YIXIN = "im.yixin";
        String YIXIN_CIRCLE = YIXIN;
        //腾讯微博
        String TENCENT = "com.tencent.WBlog";
        //Facebook
        String FACEBOOK = "com.facebook.katana";
        String FACEBOOK_MESSAGER = FACEBOOK;
        //Instagram
        String INSTAGRAM = "com.instagram.android";
        //VKontakte
        String VKONTAKTE = "com.vkontakte.android";
        //DropBox
        String DROPBOX = "com.dropbox.android";
        //Twitter
        String TWITTER = "com.twitter.android";
        //WhatsApp
        String WHATSAPP = "com.whatsapp";
        //Google+
        String GOOGLEPLUS = "com.google.android.apps.plus";
        //Line
        String LINE = "jp.naver.line.android";
        //KakaoTalk
        String KAKAO = "com.kakao.talk";
        //Pinterest
        String PINTEREST = "com.pinterest";
        //Pocket
        String POCKET = "com.ideashower.readitlater.pro";
        //Tumblr
        String TUMBLR = "com.tumblr";
        //Flickr
        String FLICKR = "com.yahoo.mobile.client.android.flickr";
        //Foursquare
        String FOURSQUARE = "com.joelapenna.foursquared";
    }

    public interface ResolveNames {
        //微信
        String WEIXIN = "com.tencent.mm.ui.tools.ShareImgUI";
        String WEIXIN_CIRCLE = WEIXIN;
        String WEIXIN_FAVORITE = "com.tencent.mm.ui.tools.AddFavoriteUI";
        //QQ
        String QQ = "com.tencent.mobileqq.activity.JumpActivity";
        //QQ轻聊版
        String QQ_LITE = "";
        //QQ国际版
        String QQ_INT = "";
        //QQ hd mini
        String QQ_HD_MINI = "";
        //QQ pad
        String QQ_PAD = "";
        //QQ空间
        String QZONE = "";
        //微博
        String SINA = "com.sina.weibo.weiyou.share.WeiyouShareDispatcher";
        //钉钉
        String DINGTALK = "";
        //支付宝
        String ALIPAY = "";
        //人人
        String RENREN = "";
        //豆瓣
        String DOUBAN = "";
        //有道云笔记
        String YNOTE = "";
        //印象笔记
        String EVERNOTE = "";
        //点点虫
        String LAIWANG = "";
        String LAIWANG_DYNAMIC = LAIWANG;
        //领英
        String LINKEDIN = "";
        //易信
        String YIXIN = "";
        String YIXIN_CIRCLE = YIXIN;
        //腾讯微博
        String TENCENT = "";
        //Facebook
        String FACEBOOK = "com.facebook.composer.shareintent.ImplicitShareIntentHandlerDefaultAlias";
        String FACEBOOK_MESSAGER = FACEBOOK;
        //Instagram
        String INSTAGRAM = "";
        //VKontakte
        String VKONTAKTE = "";
        //DropBox
        String DROPBOX = "";
        //Twitter
        String TWITTER = "";
        //WhatsApp
        String WHATSAPP = "";
        //Google+
        String GOOGLEPLUS = "";
        //Line
        String LINE = "";
        //KakaoTalk
        String KAKAO = "";
        //Pinterest
        String PINTEREST = "";
        //Pocket
        String POCKET = "";
        //Tumblr
        String TUMBLR = "";
        //Flickr
        String FLICKR = "";
        //Foursquare
        String FOURSQUARE = "";
    }
}
