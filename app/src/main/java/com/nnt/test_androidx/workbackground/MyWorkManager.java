package com.nnt.test_androidx.workbackground;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.nnt.test_androidx.workbackground.worker.SendErrorsWorker;
import com.nnt.test_worker.work.OneTimeWorkRequest;
import com.nnt.test_worker.work.WorkContinuation;
import com.nnt.test_worker.work.WorkInfo;
import com.nnt.test_worker.work.WorkManager;
import com.nnt.test_worker.work.impl.WorkManagerImpl;

import java.util.Arrays;
import java.util.List;


public class MyWorkManager {
    private WorkManager workManager;

    public MyWorkManager(Context context) {
        WorkManagerImpl.initialize(context);
        workManager = WorkManager.getInstance();
    }

    public void cancelAllWorker() {
        workManager.cancelAllWork();
    }
    public void callWorkersByTag(String tag) {
        workManager.cancelAllWorkByTag(tag);
    }

    public void sendErrorReport() {
        OneTimeWorkRequest worker1  = SendErrorsWorker.scheduleInstantly("worker 1");
        OneTimeWorkRequest worker2  = SendErrorsWorker.scheduleInstantly("worker 2");
        OneTimeWorkRequest worker3  = SendErrorsWorker.scheduleInstantly("worker 3");
        OneTimeWorkRequest worker4  = SendErrorsWorker.scheduleInstantly("worker 4");
        OneTimeWorkRequest worker5  = SendErrorsWorker.scheduleInstantly("worker 5");
        OneTimeWorkRequest worker6  = SendErrorsWorker.scheduleInstantly("worker 6");
        OneTimeWorkRequest worker7  = SendErrorsWorker.scheduleInstantly("worker 7");

        WorkContinuation workList1 = workManager
                .beginWith(Arrays.asList(worker1, worker2))
                .then(Arrays.asList(worker3, worker4))
                .then(worker5);
        WorkContinuation workList2 = workManager
                .beginWith(worker6)
                .then(worker7);
        WorkContinuation workCombine = WorkContinuation.combine(Arrays.asList(workList1, workList2));

        workCombine.enqueue();

        WorkContinuation workList3 = workManager
                .beginWith(worker6)
                .then(worker7);
        workList3.enqueue();

        LiveData<List<WorkInfo>> liveData = workCombine.getWorkInfosLiveData();
        liveData.observeForever(workInfos -> {
            assert workInfos != null;
            Log.e("TUAN", "print State: ");
            for (WorkInfo info : workInfos) {
                Log.e("TUAN", "work " + info.getId() + " __ " + info.getState());
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                workManager.cancelAllWorkByTag(SendErrorsWorker.class.getSimpleName());
            }
        }, 100*1000);
    }
}