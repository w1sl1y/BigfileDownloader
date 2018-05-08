package com.hmy.bigfiledownloader.utils;

import com.hmy.bigfiledownloader.model.RangeFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;


/**
 * Created by wesley on 2018/1/30.
 */

public class FileUtils {

    public static File getRangeFile(String tag,String dirPath){
        String apkName = tag;

        try {
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File apkFile = new File(dir, apkName);
            if (apkFile.exists()){

            }
            return apkFile;
        }catch (Exception e){
            e.printStackTrace();
        }
        return  null;
    }

    public static boolean mergeFiles(List<RangeFile> fpaths, String resultPath) {
        if (fpaths == null || fpaths.size() < 1 || null == resultPath) {
            return false;
        }
        if (fpaths.size() == 1) {
            return new File(fpaths.get(0).getFileName()).renameTo(new File(resultPath));
        }

        int lastSeprate = resultPath.lastIndexOf("/");
        if (lastSeprate > 0 && lastSeprate < resultPath.length()){
            String dir = resultPath.substring(0,lastSeprate);
            File dirFile = new File(dir);
            if (!dirFile.exists()){
                dirFile.mkdirs();
            }
        }else {
            return false;
        }

        File curFile = new File(resultPath);
        if (curFile.exists()){
            curFile.delete();
        }

        File[] files = new File[fpaths.size()];
        for (int i = 0; i < fpaths.size(); i ++) {
            files[i] = new File(fpaths.get(i).getFileName());
            if (null == fpaths.get(i).getFileName() || !files[i].exists() || !files[i].isFile()) {
                return false;
            }
        }

        File resultFile = new File(resultPath);

        try {
            FileChannel resultFileChannel = new FileOutputStream(resultFile, true).getChannel();
            for (int i = 0; i < fpaths.size(); i ++) {
                FileChannel blk = new FileInputStream(files[i]).getChannel();
                resultFileChannel.transferFrom(blk, resultFileChannel.size(), blk.size());
                blk.close();
            }
            resultFileChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        for (int i = 0; i < fpaths.size(); i ++) {
            files[i].delete();
        }

        return true;
    }


}
