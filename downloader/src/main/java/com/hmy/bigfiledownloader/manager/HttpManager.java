package com.hmy.bigfiledownloader.manager;


import com.hmy.bigfiledownloader.core.IDownloadCallback;
import com.hmy.bigfiledownloader.okhttp.GetLengthInterceptor;
import com.hmy.bigfiledownloader.okhttp.ProgressInterceptor;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

import static com.hmy.bigfiledownloader.utils.Md5.md5;

/**
 * Created by wesley on 2018/4/19.
 */

public class HttpManager {
    private OkHttpClient.Builder builder;

    private HttpManager() {
        builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addNetworkInterceptor(new ProgressInterceptor())
                .addNetworkInterceptor(new GetLengthInterceptor())
        ;
    }

    private static class HttpManagerHolder {
        private static HttpManager instance = new HttpManager();
    }

    public static HttpManager getInstance() {
        return HttpManagerHolder.instance;
    }

    /**
     * 异步获取文件长度，获取后直接关闭请求
     */
    public synchronized void getFileLength(final String url, final OnGetLength onGetLength) {

        Request request = new Request.Builder()
                .url(url)
                .addHeader("isJustForContentLen","true")
                .build();
        Call call = builder.build().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (onGetLength != null){
                    onGetLength.onGetLen(response.body().contentLength());
                }
            }
        });
    }

    public void downloadFile(String url, int tag, long start, long offset, long end, final IDownloadCallback callback) {
        Iterator<Interceptor> iterator = builder.networkInterceptors().iterator();
        while (iterator.hasNext()){
            Interceptor next = iterator.next();
            if (next instanceof GetLengthInterceptor){
                iterator.remove();
            }
        }


        String range = "bytes=" + (start + offset) + "-" + end;
        Request request = new Request.Builder()
                .url(url)
                .addHeader("HashTag", md5(url + tag + start))
                .addHeader("Range", range)
                .build();
        Call call = builder.build().newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                callback.onFailed(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    public interface OnGetLength {
        void onGetLen(long len);
    }

}
