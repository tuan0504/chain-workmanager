package com.nnt.test_worker.work.impl;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.nnt.test_worker.work.ExistingWorkPolicy;
import com.nnt.test_worker.work.OneTimeWorkRequest;
import com.nnt.test_worker.work.WorkContinuation;
import com.nnt.test_worker.work.WorkInfo;
import com.nnt.test_worker.work.WorkManager;
import com.nnt.test_worker.work.WorkRequest;
import com.nnt.test_worker.work.impl.runnable.CancelWorkRunnable;
import com.nnt.test_worker.work.impl.runnable.StartWorkRunnable;
import com.nnt.test_worker.work.impl.runnable.StopWorkRunnable;

import java.util.List;
import java.util.UUID;

public class WorkManagerImpl extends WorkManager {

    private Context mContext;
    private WorkDatabase mWorkDatabase;

    private Processor mProcessor;
    private final WorkManagerLiveDataTracker mLiveDataTracker = new WorkManagerLiveDataTracker();

    private static WorkManagerImpl sDefaultInstance = null;
    private static final Object sLock = new Object();

    public static WorkManagerImpl getInstance() {
        return sDefaultInstance;
    }

    public static void initialize(@NonNull Context context) {
        synchronized (sLock) {
            if (sDefaultInstance == null) {
                context = context.getApplicationContext();
                sDefaultInstance = new WorkManagerImpl(context);
            }
        }
    }

    public WorkManagerImpl(@NonNull Context context) {
        mContext = context.getApplicationContext();
        mWorkDatabase = new WorkDatabase();
        mProcessor = new Processor(mContext, mWorkDatabase);
    }

    public Context getApplicationContext() {
        return mContext;
    }
    public WorkDatabase getWorkDatabase() { return mWorkDatabase; }
    public Processor getProcessor() { return mProcessor; }

    @Override
    @NonNull
    public void enqueue(@NonNull List<? extends WorkRequest> workRequests) {
        if (workRequests.isEmpty()) {
            throw new IllegalArgumentException("enqueue needs at least one WorkRequest.");
        }
        new WorkContinuationImpl(this, workRequests).enqueue();
    }

    @Override
    public @NonNull WorkContinuation beginWith(@NonNull List<OneTimeWorkRequest> work) {
        if (work.isEmpty()) {
            throw new IllegalArgumentException("beginWith needs at least one OneTimeWorkRequest.");
        }
        return new WorkContinuationImpl(this, work);
    }

    @Override
    public @NonNull WorkContinuation beginUniqueWork(
            @NonNull String uniqueWorkName,
            @NonNull ExistingWorkPolicy existingWorkPolicy,
            @NonNull List<OneTimeWorkRequest> work) {
        if (work.isEmpty()) {
            throw new IllegalArgumentException("beginUniqueWork needs at least one OneTimeWorkRequest.");
        }
        return new WorkContinuationImpl(this, uniqueWorkName, existingWorkPolicy, work);
    }

    @Override
    public @NonNull void cancelWorkById(@NonNull UUID id) {
        CancelWorkRunnable runnable = CancelWorkRunnable.forId(id,this);
        mProcessor.getWorkTaskExecutor().diskIO().execute(runnable);
    }

    @Override
    public @NonNull void cancelAllWorkByTag(@NonNull final String tag) {
        CancelWorkRunnable runnable = CancelWorkRunnable.forTag(tag, this);
        mProcessor.getWorkTaskExecutor().diskIO().execute(runnable);
    }

    @Override
    @NonNull
    public void cancelUniqueWork(@NonNull String uniqueWorkName) {
        CancelWorkRunnable runnable = CancelWorkRunnable.forName(uniqueWorkName, this);
        mProcessor.getWorkTaskExecutor().diskIO().execute(runnable);
    }

    @Override
    public @NonNull void cancelAllWork() {
        CancelWorkRunnable runnable = CancelWorkRunnable.forAll(this);
        mProcessor.getWorkTaskExecutor().diskIO().execute(runnable);
    }

    public void startWork(String workSpecId) {
        StartWorkRunnable runnable = new StartWorkRunnable(this, workSpecId);
        mProcessor.getWorkTaskExecutor().diskIO().execute(runnable);
    }

    public void stopWork(String workSpecId) {
        StopWorkRunnable runnable = new StopWorkRunnable(this, workSpecId);
        mProcessor.getWorkTaskExecutor().diskIO().execute(runnable);
    }

    @Override
    public @NonNull LiveData<WorkInfo> getWorkInfoByIdLiveData(@NonNull UUID id) {
        LiveData<WorkInfo> deduped = mWorkDatabase.getWorkInfo(id.toString());
        return mLiveDataTracker.track(deduped);
    }

    @Override
    public @NonNull LiveData<List<WorkInfo>> getWorkInfosByTagLiveData(@NonNull String tag) {
        LiveData<List<WorkInfo>> deduped = mWorkDatabase.getWorkInfoByTags(tag);
        return mLiveDataTracker.track(deduped);
    }

    @Override
    @NonNull
    public LiveData<List<WorkInfo>> getWorkInfosForUniqueWorkLiveData(@NonNull String name) {
        LiveData<List<WorkInfo>> deduped = mWorkDatabase.getWorkInfoForUniqueWorkName(name);
        return mLiveDataTracker.track(deduped);
    }

    LiveData<List<WorkInfo>> getWorkInfosById(@NonNull List<String> workSpecIds) {
        LiveData<List<WorkInfo>> deduped = mWorkDatabase.getWorkInfoByListIds(workSpecIds);
        return mLiveDataTracker.track(deduped);
    }

}
