package com.dyhdyh.update.download;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.dyhdyh.update.download.listener.OnDownloadListener;

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
 * 下载服务
 * author  dengyuhan
 * created 2017/2/22 14:39
 */
@Deprecated
public class DownloadService extends Service {
    private final IBinder mBinder = new DownloadBinder();

    private OkHttpClient mOkHttpClient;
    private Call mDownloadCall;
    private OnDownloadListener mDownloadListener;

    private Handler mHandler = new Handler();

    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.mOkHttpClient = okHttpClient;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    public void stopDownload() {
        if (this.mDownloadCall != null) {
            this.mDownloadCall.cancel();
        }
        if (mDownloadListener != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mDownloadListener.OnDownloadStop();
                }
            });
        }
    }

    public void startDownload(final String downloadUrl, final File outFile) {
        if (mDownloadListener != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mDownloadListener.OnDownloadStart();
                }
            });
        }
        //下载请求
        Request request = new Request.Builder().url(downloadUrl).build();
        this.mDownloadCall = this.mOkHttpClient.newCall(request);
        this.mDownloadCall.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, final IOException e) {
                //下载失败
                if (mDownloadListener != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mDownloadListener.OnDownloadError(e);
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final InputStream is =response.body().byteStream();;
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


    public void setDownloadListener(OnDownloadListener mDownloadListener) {
        this.mDownloadListener = mDownloadListener;
    }

    public OnDownloadListener getDownloadListener() {
        return mDownloadListener;
    }

    public class DownloadBinder extends Binder {

        //返回Service对象
        public DownloadService getService() {
            return DownloadService.this;
        }
    }
}
