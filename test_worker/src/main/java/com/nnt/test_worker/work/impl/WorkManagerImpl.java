package com.nnt.test_worker.work.impl;

import android.content.Context;

import com.nnt.test_worker.work.OneTimeWorkRequest;
import com.nnt.test_worker.work.WorkContinuation;
import com.nnt.test_worker.work.WorkManager;
import com.nnt.test_worker.work.WorkRequest;
import com.nnt.test_worker.work.datatypes.ExistingWorkPolicy;
import com.nnt.test_worker.work.datatypes.WorkInfo;
import com.nnt.test_worker.work.impl.runnable.CancelWorkRunnable;

import java.util.List;
import java.util.UUID;

public class WorkManagerImpl extends WorkManager {

    private final Context mContext;
    private final WorkDatabase mWorkDatabase;
    private final Processor mProcessor;

    private static WorkManagerImpl sDefaultInstance = null;
    private static final Object sLock = new Object();

    public static WorkManagerImpl getInstance() {
        return sDefaultInstance;
    }

    public static void initialize(Context context) {
        synchronized (sLock) {
            if (sDefaultInstance == null) {
                context = context.getApplicationContext();
                sDefaultInstance = new WorkManagerImpl(context);
            }
        }
    }

    public WorkManagerImpl(Context context) {
        mContext = context.getApplicationContext();
        mWorkDatabase = new WorkDatabase();
        mProcessor = new Processor(mContext, mWorkDatabase);
    }

    public Context getApplicationContext() {
        return mContext;
    }

    public WorkDatabase getWorkDatabase() {
        return mWorkDatabase;
    }

    public Processor getProcessor() {
        return mProcessor;
    }

    @Override

    public void enqueue(List<? extends WorkRequest> workRequests) {
        if (workRequests.isEmpty()) {
            throw new IllegalArgumentException("enqueue needs at least one WorkRequest.");
        }
        new WorkContinuationImpl(this, workRequests).enqueue();
    }

    @Override
    public WorkContinuation beginWith(List<OneTimeWorkRequest> work) {
        if (work.isEmpty()) {
            throw new IllegalArgumentException("beginWith needs at least one OneTimeWorkRequest.");
        }
        return new WorkContinuationImpl(this, work);
    }

    @Override
    public WorkContinuation beginUniqueWork(
            String uniqueWorkName,
            ExistingWorkPolicy existingWorkPolicy,
            List<OneTimeWorkRequest> work) {
        if (work.isEmpty()) {
            throw new IllegalArgumentException("beginUniqueWork needs at least one OneTimeWorkRequest.");
        }
        return new WorkContinuationImpl(this, uniqueWorkName, existingWorkPolicy, work);
    }

    @Override
    public void cancelWorkById(UUID id) {
        CancelWorkRunnable runnable = CancelWorkRunnable.forId(id, this);
        mProcessor.executeBackground(runnable);
    }

    @Override
    public void cancelAllWorkByTag(final String tag) {
        CancelWorkRunnable runnable = CancelWorkRunnable.forTag(tag, this);
        mProcessor.executeBackground(runnable);
    }

    @Override

    public void cancelUniqueWork(String uniqueWorkName) {
        CancelWorkRunnable runnable = CancelWorkRunnable.forName(uniqueWorkName, this);
        mProcessor.executeBackground(runnable);
    }

    @Override
    public void cancelAllWork() {
        CancelWorkRunnable runnable = CancelWorkRunnable.forAll(this);
        mProcessor.executeBackground(runnable);
    }

    public void startWork(String workSpecId) {
        mProcessor.startWork(workSpecId);
    }

    public void stopWork(String workSpecId) {
        if (mWorkDatabase.getState(workSpecId) == WorkInfo.State.RUNNING) {
            mWorkDatabase.setState(WorkInfo.State.ENQUEUED, workSpecId);
        }
        mProcessor.stopWork(workSpecId);
    }

    public WorkDatabase.ObservableItem<List<WorkInfo>> getWorkInfosByIDs(List<String> workSpecIds) {
        return mWorkDatabase.getWorkInfoByListIds(workSpecIds);
    }
}
