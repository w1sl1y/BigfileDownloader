package com.hmy.bigfiledownloader.model;

/**
 * Created by wesley on 2018/4/24.
 */

public class RangeFile {
    private long index;

    private String fileName;

    public RangeFile(long index, String fileName) {
        this.index = index;
        this.fileName = fileName;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
