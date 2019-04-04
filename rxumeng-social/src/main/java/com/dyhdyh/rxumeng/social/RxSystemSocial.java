package com.dyhdyh.rxumeng.social;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.dyhdyh.rxumeng.social.exception.UmengPlatformInstallException;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.functions.Consumer;

/**
 * 系统分享
 * author  dengyuhan
 * created 2019/4/3 21:15
 */
public class RxSystemSocial {

    private static RxSystemSocial mInstance;

    private SHARE_MEDIA mShareMedia;
    private OnSystemSocialListener mOnSystemSocialListener;

    private RxSystemSocial() {
    }

    public static RxSystemSocial get() {
        synchronized (RxSystemSocial.class) {
            if (mInstance == null) {
                mInstance = new RxSystemSocial();
            }
            mInstance.mShareMedia = null;
        }
        return mInstance;
    }

    public RxSystemSocial setShareMedia(SHARE_MEDIA shareMedia) {
        this.mShareMedia = shareMedia;
        return this;
    }


    public RxSystemSocial setListener(OnSystemSocialListener listener) {
        this.mOnSystemSocialListener = listener;
        return this;
    }


    public RxSystemSocial setListener(Observer observer) {
        this.mOnSystemSocialListener = new OnSystemSocialListener() {
            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                observer.onError(throwable);
            }
        };
        return this;
    }


    public RxSystemSocial setListener(Consumer<Throwable> consumer) {
        this.mOnSystemSocialListener = new OnSystemSocialListener() {
            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                try {
                    consumer.accept(throwable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        return this;
    }

    /*--------系统分享的实现----------*/


    /**
     * 系统分享文字
     *
     * @param context
     * @param title
     * @param text
     * @param panelTitle
     * @param packageNames
     */
    public void startSystemShareText(Context context, String title, String text, String panelTitle, List<String> packageNames) {
        dispatchSystemShare(context, panelTitle, title, text, null, packageNames);
    }

    public void startSystemShareText(Context context, String title, String text) {
        startSystemShareText(context, title, text, null, null);
    }

    /**
     * 系统分享文件
     *
     * @param context
     * @param media
     * @param panelTitle
     * @param packageNames
     */
    public void startSystemShareFile(Context context, File media, String panelTitle, List<String> packageNames) {
        dispatchSystemShare(context, panelTitle, null, null, media, packageNames);
    }

    public void startSystemShareFile(Context context, File media) {
        startSystemShareFile(context, media, null, null);
    }

    /**
     * 系统分享图片
     *
     * @param context
     * @param image
     * @param panelTitle
     * @param packageNames
     */
    public void startSystemShareImage(Context context, UMImage image, String panelTitle, List<String> packageNames) {
        startSystemShareFile(context, image.asFileImage(), panelTitle, packageNames);
    }

    public void startSystemShareImage(Context context, UMImage image) {
        startSystemShareImage(context, image, null, null);
    }

    /**
     * 创建系统分享的Intent
     *
     * @param title
     * @param text
     * @param media
     * @return
     */
    private Intent createSystemShareIntent(Context context, @Nullable String title, @Nullable String text, @Nullable File media) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (!TextUtils.isEmpty(title)) {
            intent.putExtra(Intent.EXTRA_SUBJECT, title);
        }
        if (!TextUtils.isEmpty(text)) {
            intent.putExtra(Intent.EXTRA_TEXT, text);
        }
        if (media != null && media.exists()) {
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", media);
            } else {
                uri = Uri.fromFile(media);
            }
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            intent.putExtra(Intent.EXTRA_STREAM, uri);
            final String ext = MimeTypeMap.getFileExtensionFromUrl(media.getName());
            final String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
            intent.setType(TextUtils.isEmpty(mimeType) ? "*/*" : mimeType);
        } else {
            intent.setType("text/plain");
        }
        return intent;
    }

    /**
     * 分发系统分享
     *
     * @param context
     * @param panelTitle
     * @param title
     * @param text
     * @param media
     * @param packageNames
     * @return
     */
    private void dispatchSystemShare(@NonNull Context context, @Nullable String panelTitle, @Nullable String title, @Nullable String text, @Nullable File media, @Nullable List<String> packageNames) {
        try {
            if (mShareMedia == null || SHARE_MEDIA.MORE == mShareMedia) {
                //如果没有指定平台 就用面板方式
                startSystemShare(context, panelTitle, title, text, media, packageNames);
            } else {
                //如果指定了平台 就用系统方式单独分享平台
                startSystemShareByPlatform(context, title, text, media);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (mOnSystemSocialListener != null) {
                mOnSystemSocialListener.onError(mShareMedia, e);
            }
        }
    }

    /**
     * 单个平台的系统分享
     *
     * @param context
     * @param title
     * @param text
     * @param media
     * @return
     */
    private void startSystemShareByPlatform(@NonNull Context context, @Nullable String title, @Nullable String text, @Nullable File media) throws UmengPlatformInstallException {
        final String packageName = UmengPlatformInfo.getPackageName(mShareMedia);
        if (UmengPlatformInfo.isInstall(context, packageName)) {
            final Intent shareIntent = createSystemShareIntent(context, title, text, media);
            final String resolveName = UmengPlatformInfo.getResolveName(mShareMedia);
            if (TextUtils.isEmpty(resolveName)) {
                shareIntent.setPackage(packageName);
            } else {
                shareIntent.setComponent(new ComponentName(packageName, resolveName));
            }
            context.startActivity(shareIntent);
        } else {
            throw new UmengPlatformInstallException(mShareMedia);
        }
    }

    /**
     * 系统分享面板方式
     *
     * @param context
     * @param panelTitle   面板标题
     * @param title        分享标题
     * @param text         分享内容
     * @param media        分享文件
     * @param packageNames 只显示包名集合中的应用
     */
    private void startSystemShare(@NonNull Context context, @Nullable String panelTitle, @Nullable String title, @Nullable String text, @Nullable File media, @Nullable List<String> packageNames) {
        Intent shareIntent = createSystemShareIntent(context, title, text, media);
        Intent openInChooser;
        //过滤应用
        if (packageNames != null && !packageNames.isEmpty()) {
            PackageManager pm = context.getPackageManager();
            List<ResolveInfo> resInfo = pm.queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY);
            List<LabeledIntent> intentList = new ArrayList<>();
            for (ResolveInfo ri : resInfo) {
                //给每个应用创建Intent
                String packageName = ri.activityInfo.packageName;
                String resolveName = ri.activityInfo.name;
                Log.d("->", resolveName);
                if (packageNames.contains(packageName)) {
                    Intent itemIntent = createSystemShareIntent(context, title, text, media);
                    itemIntent.setComponent(new ComponentName(packageName, resolveName));
                    intentList.add(new LabeledIntent(itemIntent, packageName, ri.loadLabel(pm), ri.icon));
                }
            }
            openInChooser = Intent.createChooser(intentList.remove(0), panelTitle);
            openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new LabeledIntent[0]));
        } else {
            openInChooser = Intent.createChooser(shareIntent, panelTitle);
        }
        context.startActivity(openInChooser);
    }

    /*--------系统分享的实现----------*/


}
