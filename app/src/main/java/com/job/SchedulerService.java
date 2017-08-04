package com.job;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.util.Date;

/**
 * Created by Qzhu on 2017/8/3.
 * <service android:name=".SchedulerService"
 * android:permission="android.permission.BIND_JOB_SERVICE" />
 *
 * 使用方法KeepAliveActivity->schedulerJobs()
 */
public class SchedulerService extends JobService{
    private Handler handler = null;

    @Override
    public boolean onStartJob(JobParameters params) {
        HandlerThread handlerThread = new HandlerThread("SchedulerService");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper(),callback);
        Message msg = handler.obtainMessage(1,params);
        //如果有网络返回true,否则返回false
        msg.sendToTarget();
        return true;
    }
//    jobFinished(JobParameters params, boolean needsRescheduled)的两个参数中的params参数是从
//    JobService的onStartJob(JobParameters params)的params传递过来的，needsRescheduled参数是让系统知道这个任务是否应该在最处的条件下被重复执行。
//    这个boolean值很有用，因为它指明了你如何处理由于其他原因导致任务执行失败的情况，例如一个失败的网络请求调用。
    private final Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    //do something time use jobs
                    //doing...
                    Log.i("SchedulerService",new Date(System.currentTimeMillis()).toLocaleString());
                    break;
            }
            //complete
            jobFinished((JobParameters) msg.obj,true);
            handler.getLooper().quitSafely();
            return true;
        }
    };


    //主动终止需要取消正在执行的任务
    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i("SchedulerService","cancelled");
        handler.removeMessages(1);
        handler.getLooper().quitSafely();
        return false;
    }
}
