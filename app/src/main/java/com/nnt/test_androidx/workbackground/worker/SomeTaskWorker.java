package com.nnt.test_androidx.workbackground.worker;

import android.content.Context;

import com.nnt.test_worker.work.Worker;
import com.nnt.test_worker.work.datatypes.WorkerParameters;

public class SomeTaskWorker extends Worker {
    public SomeTaskWorker(Context context, WorkerParameters workerParams) {
        super(context, workerParams);
    }

    
    @Override
    public Result doWork() {
        return null;
    }
}
