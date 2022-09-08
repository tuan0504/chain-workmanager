package com.nnt.test_worker.work;

import com.nnt.test_worker.work.datatypes.ExistingWorkPolicy;
import com.nnt.test_worker.work.impl.WorkManagerImpl;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class WorkManager {

    public static WorkManager getInstance() {
        return WorkManagerImpl.getInstance();
    }


    public final void enqueue(WorkRequest workRequest) {
        enqueue(Collections.singletonList(workRequest));
    }


    public abstract void enqueue(List<? extends WorkRequest> requests);

    public final WorkContinuation beginWith(OneTimeWorkRequest work) {
        return beginWith(Collections.singletonList(work));
    }

    public abstract WorkContinuation beginWith(List<OneTimeWorkRequest> work);

    public final WorkContinuation beginUniqueWork(
            String uniqueWorkName,
            ExistingWorkPolicy existingWorkPolicy,
            OneTimeWorkRequest work) {
        return beginUniqueWork(uniqueWorkName, existingWorkPolicy, Collections.singletonList(work));
    }

    public abstract WorkContinuation beginUniqueWork(
            String uniqueWorkName,
            ExistingWorkPolicy existingWorkPolicy,
            List<OneTimeWorkRequest> work);

    public abstract void cancelWorkById(UUID id);

    public abstract void cancelAllWorkByTag(String tag);

    public abstract void cancelUniqueWork(String uniqueWorkName);

    public abstract void cancelAllWork();

    protected WorkManager() {
    }
}
