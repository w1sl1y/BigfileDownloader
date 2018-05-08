package com.hmy.testdownload;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.hmy.bigfiledownloader.R;
import com.hmy.bigfiledownloader.core.ICallback;
import com.hmy.bigfiledownloader.manager.DownloadManager;

public class MainActivity extends AppCompatActivity {
    TextView tv1;
    TextView tv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();

        tv1 = findViewById(R.id.tv);
        tv2 = findViewById(R.id.tv1);


        final String url1 = "http://1.199.93.153/imtt.dd.qq.com/16891/5FE88135737E977CCCE1A4DAC9FAFFCB.apk";
        final String url2 = "http://p.gdown.baidu.com/f4cdef7511cc83ac6e93b6d19f5e8d03b6de89e28195c78c411d5be1b39ee1959e5a4a329b742a6c688416467ae174911e7aeb1c386e16755ab49665d568a38c9594d4abc547a79e0c03aefa5015dd6e249b93dd4c2a0dc550a3a6b19f5d63edb866cbad9f3666876cc02fc27ace6104f1d75c18713820b2d59be32db79f31a777d1af1942935199077eb2ed584fb79d1538926fee90cd005ee02b5d7257567dc07be7784860720062deeb3896d1993be03c02accfd188f98868745df63afe6843b44afd73e11e3e9374a7b88f4cb9346d8e0775ba2fb139d49fb2ac5798a17ef2832c7c19173236";
        final String path1 = Environment.getExternalStorageDirectory() + "/download_apk" + "/test1.apk";
        final String path2 = Environment.getExternalStorageDirectory() + "/download_apk" + "/test2.apk";

        download(url1, path1, tv1);
        download(url2, path2, tv2);

    }

    private void download(String url, String path, final TextView textView) {
        final long start = System.currentTimeMillis();
        DownloadManager.getInstance()
                .setTempFilePath(Environment.getExternalStorageDirectory() + "/temp_download")
                .download(url, path, new ICallback() {
                    @Override
                    public void onProgress(final long len, final long total) {
                        long currentTimeMillis = System.currentTimeMillis();
                        final float speed = (float) len / (currentTimeMillis - start) / 1000;
                        final float downloadPercent = ((float) len / (float) total) * 100;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(String.format("已下载： %.2f%%，        速度：%.2f M/S", downloadPercent, speed));
                            }
                        });
                    }

                    @Override
                    public void onFailed(Throwable e) {

                    }

                    @Override
                    public void onFinish() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText("下载完成！！！");
                            }
                        });
                    }
                });
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.INTERNET}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
    }
}
