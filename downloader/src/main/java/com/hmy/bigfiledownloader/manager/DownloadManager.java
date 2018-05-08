package com.hmy.bigfiledownloader.manager;

import com.hmy.bigfiledownloader.core.DownloadTask;
import com.hmy.bigfiledownloader.core.ICallback;
import com.hmy.bigfiledownloader.core.IDownloadCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wesley on 2018/4/19.
 */

public class DownloadManager {
    private static volatile DownloadManager instance;
    //key : md5 (url + hash + start)
    private Map<String,IDownloadCallback> downloadCallbackMap;
    private String tempFilePath;


    private DownloadManager(){
        downloadCallbackMap = new HashMap<>();
    }

    public static DownloadManager getInstance(){
        if (instance == null){
            synchronized (DownloadManager.class){
                if (instance == null){
                    instance = new DownloadManager();
                }
            }
        }
        return instance;
    }

    public DownloadManager setTempFilePath(String tempFilePath) {
        this.tempFilePath = tempFilePath;
        return this;
    }

    public String getTempFilePath() {
        return tempFilePath;
    }

    public void download(final String url, String destFilePath, ICallback callback){
        DownloadTask task = new DownloadTask(url,destFilePath,callback);
        task.startDownLoad();
    }

    public void putTask(String key,IDownloadCallback callback){
        downloadCallbackMap.put(key,callback);
    }

    public IDownloadCallback getTask(String key){
        return downloadCallbackMap.get(key);
    }

    public void removeTask(String key){
        downloadCallbackMap.remove(key);
    }
}
