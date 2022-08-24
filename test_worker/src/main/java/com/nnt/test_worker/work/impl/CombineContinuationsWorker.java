package com.nnt.test_worker.work.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nnt.test_worker.work.Worker;
import com.nnt.test_worker.work.WorkerParameters;

public class CombineContinuationsWorker extends Worker {

    public CombineContinuationsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    public @NonNull
    Result doWork() {
        return Result.success(getInputData());
    }
}
