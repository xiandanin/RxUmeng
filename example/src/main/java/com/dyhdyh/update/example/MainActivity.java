package com.dyhdyh.update.example;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.dyhdyh.update.download.UpdateAgent;
import com.dyhdyh.update.download.listener.SimpleDownloadListener;
import com.dyhdyh.update.download.utils.SimpleLog;

import java.io.File;


public class MainActivity extends AppCompatActivity {
    private TextView tv_log;

    private String url="http://cdn1.lbesec.com/parallel.com/download/parallel-app-a1-vc78-vn1.0.3148-release.apk";//2M
    //private String url="http://cdn.longtugame.com/channel_bin/520006/apk/v4.1.44/520006_397.apk";//300M
    //private String url = "http://dldir1.qq.com/weixin/android/weixin6318android800.apk";//30M


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_log= (TextView) findViewById(R.id.tv_log);
        UpdateAgent.init(this);

    }

    /**
     * 更新
     * @param v
     *
     */
    public void clickUpdate(View v){
        notificationProgress(v);
        startDownload(v);
    }

    /**
     * 模拟通知栏进度
     * @param v
     */
    public void notificationProgress(View v) {
        new CountDownTimer(5000,50) {
            int progress=1;
            @Override
            public void onTick(long millisUntilFinished) {
                progress++;
                UpdateAgent.getNotificationManager().notificationDownloadProgress(100,progress);
            }

            @Override
            public void onFinish() {
                UpdateAgent.getNotificationManager().notificationDownloadComplete(new File(Environment.getExternalStorageDirectory(), "v1.apk"));
            }
        }.start();

    }


    /**
     * 下载功能
     * @param v
     */
    public void startDownload(View v) {
        UpdateAgent.getDownloadManager()
                .setDownloadListener(new SimpleDownloadListener(){
                    @Override
                    public void OnDownloadUpdate(long progress, long length) {
                        super.OnDownloadUpdate(progress,length);
                        SimpleLog.d(this,(progress/length*100)+"");
                        tv_log.setText((int)(progress*1.0/length*100)+"%");
                    }
                })
                .start(url,new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),System.currentTimeMillis() + ".apk"));
    }




    /**
     * 默认配置更新
     * @param v
     */
    public void defaultConfigUpdate(View v) {
        UpdateAgent.getInstance().update(url);
    }


    public void defaultSilentUpdate(View v) {
        UpdateAgent.getInstance().silentUpdate(url);

    }

    public void checkUpdate(View v) {
        String title = "有新版本";
        String message = "1、- 发送视频对讲后可自动保存，生成专属表情包\n" +
                "2、- 推荐你可能认识的人，发现更多新朋友\n" +
                "3、- 口令红包消息超过十二条时，可自动折叠\n" +
                "4、- 多条消息被合并转发后，可再次转发。";

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("稍后再说", null)
                .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Download.start(MainActivity.this, url, "1.apk");
                    }
                });
        builder.show();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        UpdateAgent.getInstance().destroy();
    }

}
