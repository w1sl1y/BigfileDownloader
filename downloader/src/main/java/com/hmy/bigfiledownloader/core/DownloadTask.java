package com.hmy.bigfiledownloader.core;


import com.hmy.bigfiledownloader.manager.DownloadManager;
import com.hmy.bigfiledownloader.manager.HttpManager;
import com.hmy.bigfiledownloader.model.MergeFileFailExecption;
import com.hmy.bigfiledownloader.model.RangeFile;
import com.hmy.bigfiledownloader.utils.FileUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wesley on 2018/4/24.
 */

public class DownloadTask implements IDownloadCallback {
    private static final String TAG = "DownloadTask";
    private long totalLen;
    private String url;
    private long download = 0;
    private static final int THREAD_COUNT = 2;
    private ICallback callback;
    private String destFilePath;
    private List<SubTask> taskList = new ArrayList<>();
    private List<RangeFile> files = new ArrayList<>();

    public DownloadTask(String url,String destFilePath, ICallback callback) {
        this.url = url;
        this.destFilePath = destFilePath;
        this.callback = callback;
    }

    public void startDownLoad() {
        final int hash = hashCode();
        HttpManager.getInstance().getFileLength(url, new HttpManager.OnGetLength() {
            @Override
            public void onGetLen(long len) {
                System.out.println(url + " file lenth:   " + len);
                totalLen = len;
                long unit = totalLen / THREAD_COUNT;

                for (int i = 0; i < THREAD_COUNT; i++) {
                    long start = i * unit;
                    long total = 0;
                    if (i < THREAD_COUNT - 1) {
                        total = unit;
                    } else {
                        total = totalLen - start;
                    }

                    SubTask subTask = new SubTask(url, hash, start, total, DownloadTask.this);
                    taskList.add(subTask);
                    subTask.startDownLoad();
                }
            }
        });
    }


    @Override
    public void onProgress(long len) {
        download += len;
        if (callback != null)
            callback.onProgress(download, totalLen);
    }

    @Override
    public void onFailed(Throwable e) {
        if (callback != null)
            callback.onFailed(e);
    }

    @Override
    public synchronized void onFinish(long index, String fileName) {
        Iterator<SubTask> iterator = taskList.iterator();
        while (iterator.hasNext()) {
            SubTask task = iterator.next();
            if (task.getFileName().equals(fileName)) {
                iterator.remove();
            }
        }

        RangeFile rangeFile = new RangeFile(index, DownloadManager.getInstance().getTempFilePath() + "/" + fileName);
        int fileIndex = findIndex(rangeFile);

        files.add(fileIndex, rangeFile);
        System.out.println(files.size() + " /  " + THREAD_COUNT +" "+  destFilePath + " complete!!");

        if (taskList.size() == 0) {
            boolean success = mergeFile();

            if (callback != null) {
                if (success) {
                    callback.onFinish();
                } else {
                    callback.onFailed(new MergeFileFailExecption());
                }
            }
        }
    }

    private int findIndex(RangeFile rangeFile) {
        int i = 0;
        for (; i < files.size(); i++) {
            if (rangeFile.getIndex() <= files.get(i).getIndex()) {
                return i;
            }
        }
        return i;
    }

    private boolean mergeFile() {
        boolean success = FileUtils.mergeFiles(files, destFilePath);
        files.clear();
        return success;
    }

}
