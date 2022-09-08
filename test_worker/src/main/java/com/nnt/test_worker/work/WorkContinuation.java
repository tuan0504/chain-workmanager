package com.nnt.test_worker.work;

import com.nnt.test_worker.work.datatypes.WorkInfo;
import com.nnt.test_worker.work.impl.WorkDatabase;

import java.util.Collections;
import java.util.List;

public abstract class WorkContinuation {

    public final WorkContinuation then(OneTimeWorkRequest work) {
        return then(Collections.singletonList(work));
    }

    public abstract WorkContinuation then(List<OneTimeWorkRequest> work);

    public static WorkContinuation combine(List<WorkContinuation> continuations) {
        return continuations.get(0).combineInternal(continuations);
    }

    protected abstract WorkContinuation combineInternal(List<WorkContinuation> continuations);

    public abstract WorkDatabase.ObservableItem<List<WorkInfo>> getWorkInfosLiveData();

    public abstract void enqueue();
}
