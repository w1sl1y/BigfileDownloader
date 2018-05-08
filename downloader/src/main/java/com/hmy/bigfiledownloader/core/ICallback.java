package com.hmy.bigfiledownloader.core;

/**
 * Created by wesley on 2018/4/24.
 */

public interface ICallback {

    void onProgress(long len,long total);

    void onFailed(Throwable e);

    void onFinish();
}
