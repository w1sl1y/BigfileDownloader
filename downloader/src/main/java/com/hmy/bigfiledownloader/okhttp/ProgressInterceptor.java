package com.hmy.bigfiledownloader.okhttp;


import com.hmy.bigfiledownloader.core.IDownloadCallback;
import com.hmy.bigfiledownloader.manager.DownloadManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by wesley on 2018/1/30.
 */

public class ProgressInterceptor implements Interceptor {

    public ProgressInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        String tag = chain.request().header("HashTag");

        if (originalResponse.header("Content-Range") != null) {
            String contentRange = originalResponse.header("Content-Range");
            String rangeStr = contentRange.split("/")[0];
            rangeStr = rangeStr.replace("bytes ", "");
            String[] splitRange = rangeStr.split("-");
            long start = Long.parseLong(splitRange[0]);
            long end = Long.parseLong(splitRange[1]);

            IDownloadCallback callback = DownloadManager.getInstance().getTask(tag);
            return originalResponse.newBuilder()
                    .body(new ProgressResponseBody(originalResponse.body(), tag, start, end - start + 1, callback))
                    .build();
        }
        return originalResponse;
    }
}
