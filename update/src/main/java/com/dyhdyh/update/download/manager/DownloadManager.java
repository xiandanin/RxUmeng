package com.dyhdyh.update.download.manager;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.dyhdyh.update.download.UpdateAgent;
import com.dyhdyh.update.download.listener.OnDownloadListener;
import com.dyhdyh.update.download.listener.OnUpdateDownloadListener;
import com.dyhdyh.update.download.utils.SimpleLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 下载器
 * author  dengyuhan
 * created 2017/2/22 16:52
 */
public class DownloadManager {
    private Context mContext;
    private OkHttpClient mOkHttpClient;
    private Call mDownloadCall;
    private OnDownloadListener mDownloadListener;

    public DownloadManager(Context context) {
        this.mContext = context;
        if (mDownloadListener == null) {
            setDownloadListener(new OnUpdateDownloadListener(mContext));
        }
    }

    public void start(String downloadUrl) {
        String apkFileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"), downloadUrl.length());
        String defaultApkFileName = System.currentTimeMillis() + ".apk";
        start(downloadUrl, new File(UpdateAgent.getUpdateConfig().getDownloadDirectory(), TextUtils.isEmpty(apkFileName) ? defaultApkFileName : apkFileName));
    }


    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.mOkHttpClient = okHttpClient;
    }

    public DownloadManager setDownloadListener(OnDownloadListener mDownloadListener) {
        this.mDownloadListener = mDownloadListener;
        return this;
    }

    public void stop() {
        SimpleLog.d(this, "停止下载");
        if (this.mDownloadCall != null) {
            this.mDownloadCall.cancel();
        }
        if (mDownloadListener != null) {
            mDownloadListener.OnDownloadStop();
        }
    }

    public void start(final String downloadUrl, final File outFile) {
        SimpleLog.d(this, "开始下载-----" + downloadUrl);
        if (mDownloadListener != null) {
            mDownloadListener.OnDownloadStart();
        }
        //下载请求
        Request request = new Request.Builder().url(downloadUrl).build();
        this.mDownloadCall = this.mOkHttpClient.newCall(request);
        this.mDownloadCall.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, final IOException e) {
                //下载失败
                if (mDownloadListener != null) {
                    mDownloadListener.OnDownloadError(e);
                }
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final InputStream is = response.body().byteStream();
                ;
                final long length = response.body().contentLength();
                new AsyncTask<Response, Long, File>() {

                    @Override
                    protected File doInBackground(Response... params) {
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(outFile);
                            int len = 0;
                            long sum = 0;
                            byte[] buffer = new byte[2048];
                            while ((len = is.read(buffer)) != -1) {
                                fos.write(buffer, 0, len);
                                sum += len;
                                this.publishProgress(sum);
                            }
                            fos.flush();
                            return outFile;
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (fos != null) {
                                    fos.close();
                                }
                                if (is != null) {
                                    is.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void onProgressUpdate(Long... values) {
                        //下载进度
                        if (mDownloadListener != null) {
                            mDownloadListener.OnDownloadUpdate(values[0], length);
                        }
                    }

                    @Override
                    protected void onPostExecute(File file) {
                        //下载完成
                        if (mDownloadListener != null) {
                            mDownloadListener.OnDownloadEnd(length, file);
                        }
                    }
                }.execute(response);
            }


        });
    }


}
