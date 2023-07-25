package com.example.camera;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        
    }

    //调起相机
    public void camera(View view) {
        startActivity(new Intent(this, CameraActivity.class));
    }

    //创建线程池
    private void createExecutorService() {
        //固定线程池
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
        //execute和submit区别
        //1. execute只能提交Runnable类型的任务，没有返回值，而submit既能提交Runnable类型任务也能提交Callable类型任务，返回Future类型。
        //2. execute方法提交的任务异常是直接抛出的，而submit方法是是捕获了异常的，当调用FutureTask的get方法时，才会抛出异常
        fixedThreadPool.submit(new TaskRunnable());
        fixedThreadPool.execute(new TaskRunnable());

        //缓存线程池
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        cachedThreadPool.submit(new TaskRunnable());

        //顺序线程池
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        singleThreadExecutor.submit(new TaskRunnable());

        //周期定时线程池
        ExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
        scheduledThreadPool.submit(new TaskRunnable());
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


//    //创建线程池
//    public void createExecutorService(){
//        //maximumPoolSize设置为2 ，拒绝策略为AbortPolic策略，直接抛出异常
//        //线程池能执行的最大任务数为3（最大线程数）+0（队列长度）   SynchronousQueue没有容量
//        ExecutorService executorService = new ThreadPoolExecutor(2, 3, 1000, TimeUnit.MILLISECONDS, new SynchronousQueue<>(), Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
//        //添加并执行线程
//        executorService.execute(new TaskRunnable());
//    }


//    //创建线程池工厂
//    public void createExecutorService(){
//        //maximumPoolSize设置为2 ，拒绝策略为AbortPolic策略，直接抛出异常
//        //线程池能执行的最大任务数为3（最大线程数）+0（队列长度）   SynchronousQueue没有容量
//        ExecutorService executorService = new ThreadPoolExecutor(2, 3, 1000, TimeUnit.MILLISECONDS, new SynchronousQueue<>(),
//                //线程池工厂
//                new ThreadFactory() {
//                    public Thread newThread(Runnable runnable) {
//                        //可以新建线程，进行命名、优先级等设置
//                        Log.e("taskRunnable", "newThread: "+"线程"+runnable.hashCode()+"创建" );
//                        //线程命名
//                        Thread thread = new Thread(runnable,"threadPool"+runnable.hashCode());
//                        return thread;
//                    }
//                },
//                new ThreadPoolExecutor.AbortPolicy());
//        //添加并执行线程
//        executorService.execute(new TaskRunnable());
//    }


//饱和策略的使用
//    public void createExecutorService() {
//        //maximumPoolSize设置为2 ，拒绝策略为AbortPolic策略，直接抛出异常
//        //线程池能执行的最大任务数为3（最大线程数）+0（队列长度）   SynchronousQueue没有容量
//        ExecutorService executorService = new ThreadPoolExecutor(2, 3, 1000, TimeUnit.MILLISECONDS, new SynchronousQueue<>(), Executors.defaultThreadFactory(),
//                //拒绝策略
//                new RejectedExecutionHandler() {
//                    @Override
//                    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
//                        Log.e("taskRunnable", runnable.toString()+"执行了拒绝策略");
//                    }
//                }
//        );
//        //添加并执行线程
//        executorService.execute(new TaskRunnable());
//
//    }


//创建任务线程
class TaskRunnable implements Runnable {
    @Override
    public void run() {
        Log.e("taskRunnable", "run: " + Thread.currentThread().getName() + "---" + new Date());
    }
}

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //创建异步任务
    private void createAsyncTask() {
        //可以用内置线程池AsyncTask.SERIAL_EXECUTOR(单线程顺序执行)，AsyncTask.THREAD_POOL_EXECUTOR(多线程并发执行)
        //也可以用自定义线程池newCachedThreadPool，newFixedThreadPool，newScheduledThreadPool，newSingleThreadExecutor
        new MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "1", "2", "3");
    }

/**
 * 第一个参数String类型，可以传单个类型也可以传类型数组，doInBackground里面获取的参数
 * 第二个参数Float类型，任务进度 onProgressUpdate获取
 * 第三个参数String类型，处理结果 onPostExecute 获取
 */
public class MyAsyncTask extends AsyncTask<String, Float, String> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //准备执行，可以做一些准备工作，比如弹缓冲框，初始化数据等
    }

    @Override
    protected String doInBackground(String... strings) {
        //String参数数组，传几个就接受几个
        //处理耗时任务，子线程
        //返回处理后的结果
        String param1 = strings[1];
        String param2 = strings[1];
        String param3 = strings[1];
        return null;
    }

    @Override
    protected void onProgressUpdate(Float... values) {
        super.onProgressUpdate(values);
        //任务处理进度
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //执行结果返回，UI线程
    }


    @Override
    protected void onCancelled() {
        super.onCancelled();
        //处理异步任务
    }
}

}