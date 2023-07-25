package com.dinghe.servicetest.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

/**
 * @ClassName TestJobIntentService
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/5/15 17:22
 * @Version 1.0
 * @UpdateDate 2023/5/15 17:22
 * @UpdateRemark 更新说明
 */

public class TestJobService extends JobService {
    private static final String TAG = "TestJobService";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        work.start();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    private Thread work = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.e(TAG, "finished !");
        }
    });

}
