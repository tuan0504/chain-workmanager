package com.nnt.test_androidx.workbackground.worker;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nnt.test_worker.work.Worker;
import com.nnt.test_worker.work.WorkerParameters;

public class someTaskWorker extends Worker {
    public someTaskWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        return null;
    }
}
