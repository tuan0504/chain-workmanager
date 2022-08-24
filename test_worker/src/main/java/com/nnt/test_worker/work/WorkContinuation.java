package com.nnt.test_worker.work;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;

public abstract class WorkContinuation {

    public final @NonNull
    WorkContinuation then(@NonNull OneTimeWorkRequest work) {
        return then(Collections.singletonList(work));
    }

    public abstract @NonNull
    WorkContinuation then(@NonNull List<OneTimeWorkRequest> work);

    public abstract @NonNull
    LiveData<List<WorkInfo>> getWorkInfosLiveData();

    public abstract @NonNull void enqueue();

    public static @NonNull
    WorkContinuation combine(@NonNull List<WorkContinuation> continuations) {
        return continuations.get(0).combineInternal(continuations);
    }

    protected abstract @NonNull
    WorkContinuation combineInternal(@NonNull List<WorkContinuation> continuations);
}
