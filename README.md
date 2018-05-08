大文件下载工具库


实现流程
1 有一个下载任务时，先获取文件下载总长度
2 将该任务拆分为N个子任务，每个子任务下载其对应长度的子文件并保存
3 下载任务监听所有子任务的完成情况，如果全部完成则合并所有的子文件为目标文件
4  回调给用户，下载任务结束


示例：



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
