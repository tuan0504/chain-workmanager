package com.nnt.test_worker.work;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;

import com.nnt.test_worker.work.impl.WorkManagerImpl;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class WorkManager {

    public static @NonNull
    WorkManager getInstance() {
        return WorkManagerImpl.getInstance();
    }

    @NonNull
    public final void enqueue(@NonNull WorkRequest workRequest) {
        enqueue(Collections.singletonList(workRequest));
    }

    @NonNull
    public abstract void enqueue(@NonNull List<? extends WorkRequest> requests);

    public final @NonNull
    WorkContinuation beginWith(@NonNull OneTimeWorkRequest work) {
        return beginWith(Collections.singletonList(work));
    }

    public abstract @NonNull
    WorkContinuation beginWith(@NonNull List<OneTimeWorkRequest> work);

    public final @NonNull
    WorkContinuation beginUniqueWork(
            @NonNull String uniqueWorkName,
            @NonNull ExistingWorkPolicy existingWorkPolicy,
            @NonNull OneTimeWorkRequest work) {
        return beginUniqueWork(uniqueWorkName, existingWorkPolicy, Collections.singletonList(work));
    }

    public abstract @NonNull
    WorkContinuation beginUniqueWork(
            @NonNull String uniqueWorkName,
            @NonNull ExistingWorkPolicy existingWorkPolicy,
            @NonNull List<OneTimeWorkRequest> work);

    public abstract @NonNull
    void cancelWorkById(@NonNull UUID id);

    public abstract @NonNull
    void cancelAllWorkByTag(@NonNull String tag);

    public abstract @NonNull
    void cancelUniqueWork(@NonNull String uniqueWorkName);

    public abstract @NonNull
    void cancelAllWork();

    public abstract @NonNull
    LiveData<WorkInfo> getWorkInfoByIdLiveData(@NonNull UUID id);

    public abstract @NonNull
    LiveData<List<WorkInfo>> getWorkInfosByTagLiveData(@NonNull String tag);

    public abstract @NonNull
    LiveData<List<WorkInfo>> getWorkInfosForUniqueWorkLiveData(@NonNull String uniqueWorkName);

    /**
     * @hide
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    protected WorkManager() {
    }
}
