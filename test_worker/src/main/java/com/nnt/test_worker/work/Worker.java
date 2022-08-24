package com.nnt.test_worker.work;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

public abstract class Worker extends ListenableWorker {

    public Worker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @WorkerThread
    public abstract @NonNull Result doWork();
}
