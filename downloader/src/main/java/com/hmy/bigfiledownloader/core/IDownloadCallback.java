package com.hmy.bigfiledownloader.core;

/**
 * Created by wesley on 2018/4/24.
 */

public interface IDownloadCallback {

    void onProgress(long len);

    void onFailed(Throwable e);

    void onFinish(long index,String fileName);
}
