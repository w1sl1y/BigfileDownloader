package com.hmy.bigfiledownloader.core;

import com.hmy.bigfiledownloader.manager.DownloadManager;
import com.hmy.bigfiledownloader.manager.HttpManager;

import static com.hmy.bigfiledownloader.utils.Md5.md5;

/**
 * Created by wesley on 2018/4/24.
 */

public class SubTask implements IDownloadCallback{
    private static final String TAG = "SubTask";
    private IDownloadCallback callback;
    private long total;
    private long start;
    private String url;
    private int tag;
    private String fileName ;
    private long download = 0;

    public SubTask(String url,int tag, long start, long total,IDownloadCallback callback) {
        this.url = url;
        this.start = start;
        this.total = total;
        this.callback = callback;
        this.tag = tag;
    }

    public void startDownLoad(){
        fileName = md5(url+tag+start);
        HttpManager.getInstance().downloadFile(url,tag,start,0,start + total -1,this);
        DownloadManager.getInstance().putTask(fileName,this);
    }

    @Override
    public void onProgress(long len) {
        download += len;
        callback.onProgress(len);
    }

    @Override
    public void onFailed(Throwable e) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        HttpManager.getInstance().downloadFile(url,tag,start ,download,start + total - 1 ,this);
    }

    @Override
    public void onFinish(long index,String fileName) {
        DownloadManager.getInstance().removeTask(fileName);
        callback.onFinish(index,fileName);
    }

    public String getFileName() {
        return fileName;
    }
}
