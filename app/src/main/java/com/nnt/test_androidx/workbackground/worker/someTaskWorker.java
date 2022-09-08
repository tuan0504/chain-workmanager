package com.nnt.test_androidx.workbackground.worker;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nnt.test_worker.work.Worker;
import com.nnt.test_worker.work.datatypes.WorkerParameters;

public class someTaskWorker extends Worker {
    public someTaskWorker( Context context,  WorkerParameters workerParams) {
        super(context, workerParams);
    }

    
    @Override
    public Result doWork() {
        return null;
    }
}
