package com.nnt.test_worker.work;

import android.content.Context;

import com.nnt.test_worker.work.datatypes.WorkerParameters;

public class CombineContinuationsWorker extends Worker {

    public CombineContinuationsWorker(Context context, WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    public Result doWork() {
        return Result.success(getInputData());
    }
}
