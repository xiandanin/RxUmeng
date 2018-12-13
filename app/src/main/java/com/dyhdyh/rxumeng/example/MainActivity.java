package com.dyhdyh.rxumeng.example;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dyhdyh.rxumeng.social.RxUmengSocial;
import com.dyhdyh.rxumeng.social.UmengAuthResult;
import com.dyhdyh.rxumeng.social.exception.UmengPlatformCancelException;
import com.dyhdyh.rxumeng.social.exception.UmengPlatformInstallException;
import com.dyhdyh.widget.loading.dialog.LoadingDialog;
import com.google.gson.GsonBuilder;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

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

    /**
     * 第三方登录
     *
     * @param shareMedia
     */
    private void clickPlatformLogin(SHARE_MEDIA shareMedia) {
        if (!RxUmengSocial.get().hasPermissions(this)){
            return;
        }
        RxUmengSocial.get()
                .setShareMedia(shareMedia)
                .getPlatformInfo(this)
                .subscribe(new Observer<UmengAuthResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LoadingDialog.make(MainActivity.this).show();
                    }

                    @Override
                    public void onNext(UmengAuthResult result) {
                        LoadingDialog.cancel();

                        tv_log.setText(new GsonBuilder()
                                .setPrettyPrinting()
                                .create().toJson(result));
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoadingDialog.cancel();
                        if (e instanceof UmengPlatformInstallException) {
                            Toast.makeText(MainActivity.this, "没有安装" + ((UmengPlatformInstallException) e).getShareMedia() + "客户端", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof UmengPlatformCancelException) {
                            Toast.makeText(MainActivity.this, "取消登录", Toast.LENGTH_SHORT).show();
                        } else {
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
        if (!RxUmengSocial.get().hasPermissions(this)){
            return;
        }
        RxUmengSocial.get()
                .setShareMedia(shareMedia)
                .shareUrl(this, "我只是个标题", "我也是只是个内容", new UMImage(this, R.mipmap.ic_launcher), "https://github.com/dengyuhan/RxUmeng")
                .subscribe(new Observer<SHARE_MEDIA>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LoadingDialog.make(MainActivity.this).show();
                    }

                    @Override
                    public void onNext(SHARE_MEDIA result) {
                        LoadingDialog.cancel();

                        Toast.makeText(MainActivity.this, result + "分享成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoadingDialog.cancel();
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
