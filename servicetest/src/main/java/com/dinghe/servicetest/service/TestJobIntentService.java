package com.dinghe.servicetest.service;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

/**
 * @ClassName TestJobIntentService
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/5/15 18:20
 * @Version 1.0
 * @UpdateDate 2023/5/15 18:20
 * @UpdateRemark 更新说明
 */
public class TestJobIntentService extends JobIntentService {
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        //耗时任务
    }
}
