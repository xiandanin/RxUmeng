package com.dyhdyh.rxumeng.example;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dyhdyh.rxumeng.social.OnSystemSocialListener;
import com.dyhdyh.rxumeng.social.RxSystemSocial;
import com.dyhdyh.rxumeng.social.RxUmengSocial;
import com.dyhdyh.rxumeng.social.UmengAuthResult;
import com.dyhdyh.rxumeng.social.UmengPlatformInfo;
import com.dyhdyh.rxumeng.social.exception.UmengPlatformCancelException;
import com.dyhdyh.rxumeng.social.exception.UmengPlatformInstallException;
import com.dyhdyh.widget.loadingbar2.LoadingBar;
import com.google.gson.GsonBuilder;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {
    TextView tv_log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_log = findViewById(R.id.tv_log);

    }

    public void clickWechatLogin(View view) {
        clickPlatformLogin(SHARE_MEDIA.WEIXIN);
    }

    public void clickQQLogin(View view) {
        clickPlatformLogin(SHARE_MEDIA.QQ);
    }

    public void clickWeiboLogin(View view) {
        clickPlatformLogin(SHARE_MEDIA.SINA);
    }

    public void clickWechatShare(View view) {
        clickPlatformShare(SHARE_MEDIA.WEIXIN);
    }

    public void clickCircleShare(View view) {
        clickPlatformShare(SHARE_MEDIA.WEIXIN_CIRCLE);
    }

    public void clickWeiboShare(View view) {
        clickPlatformShare(SHARE_MEDIA.SINA);
    }

    public void clickQQShare(View view) {
        clickPlatformShare(SHARE_MEDIA.QQ);
    }

    public void clickQZoneShare(View view) {
        clickPlatformShare(SHARE_MEDIA.QZONE);
    }


    public void clickSystemShare(View view) {
        clickStartSystemShare(null);
    }

    public void clickSystemShareFilter(View view) {
        List<String> packageNames = UmengPlatformInfo.getPackageNames(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.QQ);
        RxSystemSocial.get().startSystemShareText(this, "我只是个标题", "我也是只是个内容", "系统分享(只列出微信QQ)", packageNames);
    }

    public void clickSystemShareWechat(View view) {
        clickStartSystemShare(SHARE_MEDIA.WEIXIN);
    }

    public void clickSystemShareQQ(View view) {
        clickStartSystemShare(SHARE_MEDIA.QQ);
    }


    public void clickPlatformList(MenuItem item) {
        final SHARE_MEDIA[] values = SHARE_MEDIA.values();
        StringBuilder sb = new StringBuilder();
        for (SHARE_MEDIA shareMedia : values) {
            final String packageName = UmengPlatformInfo.getPackageName(shareMedia);
            sb.append(shareMedia);
            sb.append("  |  ");
            sb.append(packageName);
            sb.append("\n");
        }
        tv_log.setText(sb.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    /**
     * 系统分享
     *
     * @param shareMedia
     */
    private void clickStartSystemShare(SHARE_MEDIA shareMedia) {
        RxSystemSocial.get()
                .setShareMedia(shareMedia)
                .setListener(new OnSystemSocialListener() {
                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable e) {
                        if (e instanceof UmengPlatformInstallException) {
                            Toast.makeText(MainActivity.this, "没有安装" + share_media + "客户端", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof ActivityNotFoundException) {
                            //这个平台通过系统分享 分享不了这个内容
                            Toast.makeText(MainActivity.this, share_media + "不支持分享该内容", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .startSystemShareText(this, "我只是个标题", "我也是只是个内容");
    }

    /**
     * 第三方登录
     *
     * @param shareMedia
     */
    private void clickPlatformLogin(SHARE_MEDIA shareMedia) {
        //检查权限(没有会先申请)
        if (!RxUmengSocial.get().hasPermissions(this)) {
            return;
        }
        RxUmengSocial.get()
                .setShareMedia(shareMedia)
                .getPlatformInfo(this)
                .subscribe(new Observer<UmengAuthResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LoadingBar.dialog(MainActivity.this).show();
                    }

                    @Override
                    public void onNext(UmengAuthResult result) {
                        LoadingBar.dialog(MainActivity.this).cancel();

                        tv_log.setText(new GsonBuilder()
                                .setPrettyPrinting()
                                .create().toJson(result));
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoadingBar.dialog(MainActivity.this).cancel();

                        if (e instanceof UmengPlatformInstallException) {
                            //没有安装需要的客户端的异常(微博可以用网页授权，所以微博不会检查)
                            Toast.makeText(MainActivity.this, "没有安装" + ((UmengPlatformInstallException) e).getShareMedia() + "客户端", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof UmengPlatformCancelException) {
                            //用户取消操作会回调这里
                            Toast.makeText(MainActivity.this, "取消登录", Toast.LENGTH_SHORT).show();
                        } else {
                            //其它失败的异常
                            Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void clickPlatformShare(SHARE_MEDIA shareMedia) {
        tv_log.setText("");
        if (!RxUmengSocial.get().hasPermissions(this)) {
            return;
        }
        //检查平台是否可用
        if (!RxUmengSocial.get().isPlatformAvailable(this, shareMedia)) {
            Toast.makeText(MainActivity.this, "没有安装" + shareMedia + "客户端", Toast.LENGTH_SHORT).show();
            return;
        }
        RxUmengSocial.get()
                .setCheckPlatform(false)//前面手动检查了 把自动检查设为false
                .setShareMedia(shareMedia)
                //图片不能用mipmap
                .shareUrl(this, "我只是个标题", "我也是只是个内容", new UMImage(this, R.drawable.logo), "https://github.com/dengyuhan/RxUmeng")
                .subscribe(new Observer<SHARE_MEDIA>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LoadingBar.dialog(MainActivity.this).show();
                    }

                    @Override
                    public void onNext(SHARE_MEDIA result) {
                        LoadingBar.dialog(MainActivity.this).cancel();

                        //检查平台是否有成功事件
                        if (UmengPlatformInfo.isHasShareSuccessEvent(result)) {
                            Toast.makeText(MainActivity.this, result + "分享成功", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();

                        LoadingBar.dialog(MainActivity.this).cancel();

                        if (e instanceof UmengPlatformInstallException) {
                            Toast.makeText(MainActivity.this, "没有安装" + ((UmengPlatformInstallException) e).getShareMedia() + "客户端", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof UmengPlatformCancelException) {
                            Toast.makeText(MainActivity.this, "取消分享", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    /*--------Activity 要重写的方法----------*/

    @Override
    protected void onResume() {
        super.onResume();
        RxUmengSocial.get().onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxUmengSocial.get().onDestroy(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        RxUmengSocial.get().onActivityResult(this, requestCode, resultCode, data);
    }

}
