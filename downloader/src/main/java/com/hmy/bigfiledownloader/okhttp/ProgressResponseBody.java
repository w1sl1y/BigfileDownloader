package com.hmy.bigfiledownloader.okhttp;


import com.hmy.bigfiledownloader.core.IDownloadCallback;
import com.hmy.bigfiledownloader.manager.DownloadManager;
import com.hmy.bigfiledownloader.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by tinyfight on 2018/1/18.
 */

public class ProgressResponseBody extends ResponseBody {
    private static final String TAG = "ProgressResponseBody";
    private final ResponseBody responseBody;
    private BufferedSource bufferedSource;
    private File rangeFile;
    private String tag;
    private long start;
    private OutputStream out;


    public static long total;
    private long loaded;

    private IDownloadCallback callback;

    public ProgressResponseBody(ResponseBody body, String tag, long start, long total, IDownloadCallback callback) {
        responseBody = body;
        this.callback = callback;
        this.total = total;
        this.start = start;
        this.tag = tag;
        rangeFile = FileUtils.getRangeFile(tag, DownloadManager.getInstance().getTempFilePath());
        try {
            out = new FileOutputStream(rangeFile, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(" " + start + " - " + (start + total) + " started");

    }


    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {

        return new ForwardingSource(source) {
            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                if (bytesRead != -1) {
                    loaded += bytesRead;
                    sink.writeTo(out);
                    sink.flush();

                    if (callback != null) {
                        callback.onProgress(bytesRead);
                    }
                } else {
                    if (callback != null)
                        callback.onFinish(start, tag);
                }

                return bytesRead;
            }
        };
    }

}
