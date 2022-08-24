package com.nnt.test_worker.work.impl;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;

import com.nnt.test_worker.work.ExistingWorkPolicy;
import com.nnt.test_worker.work.OneTimeWorkRequest;
import com.nnt.test_worker.work.WorkContinuation;
import com.nnt.test_worker.work.WorkInfo;
import com.nnt.test_worker.work.WorkRequest;
import com.nnt.test_worker.work.impl.runnable.EnqueueRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A concrete implementation of {@link WorkContinuation}.
 *
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class WorkContinuationImpl extends WorkContinuation {

    private final WorkManagerImpl mWorkManagerImpl;
    private final String mName;
    private final ExistingWorkPolicy mExistingWorkPolicy;
    private final List<? extends WorkRequest> mWork;
    private final List<String> mIds;
    private final List<String> mAllIds;
    private final List<WorkContinuationImpl> mParents;

    private boolean mEnqueued;

    @NonNull
    public WorkManagerImpl getWorkManagerImpl() {
        return mWorkManagerImpl;
    }

    @Nullable
    public String getName() {
        return mName;
    }

    public ExistingWorkPolicy getExistingWorkPolicy() {
        return mExistingWorkPolicy;
    }

    @NonNull
    public List<? extends WorkRequest> getWork() {
        return mWork;
    }

    @NonNull
    public List<String> getIds() {
        return mIds;
    }

    public List<String> getAllIds() {
        return mAllIds;
    }

    public boolean isEnqueued() {
        return mEnqueued;
    }

    public void markEnqueued() {
        mEnqueued = true;
    }

    public List<WorkContinuationImpl> getParents() {
        return mParents;
    }

    WorkContinuationImpl(
            @NonNull WorkManagerImpl workManagerImpl,
            @NonNull List<? extends WorkRequest> work) {
        this(
                workManagerImpl,
                null,
                ExistingWorkPolicy.KEEP,
                work,
                null);
    }

    WorkContinuationImpl(
            @NonNull WorkManagerImpl workManagerImpl,
            String name,
            ExistingWorkPolicy existingWorkPolicy,
            @NonNull List<? extends WorkRequest> work) {
        this(workManagerImpl, name, existingWorkPolicy, work, null);
    }

    WorkContinuationImpl(@NonNull WorkManagerImpl workManagerImpl,
                         String name,
                         ExistingWorkPolicy existingWorkPolicy,
                         @NonNull List<? extends WorkRequest> work,
                         @Nullable List<WorkContinuationImpl> parents) {
        mWorkManagerImpl = workManagerImpl;
        mName = name;
        mExistingWorkPolicy = existingWorkPolicy;
        mWork = work;
        mParents = parents;
        mIds = new ArrayList<>(mWork.size());
        mAllIds = new ArrayList<>();
        if (parents != null) {
            for (WorkContinuationImpl parent : parents) {
                mAllIds.addAll(parent.mAllIds);
            }
        }
        for (int i = 0; i < work.size(); i++) {
            String id = work.get(i).getStringId();
            mIds.add(id);
            mAllIds.add(id);
        }
    }

    @Override
    public @NonNull
    WorkContinuation then(List<OneTimeWorkRequest> work) {
        return new WorkContinuationImpl(mWorkManagerImpl, mName,
                ExistingWorkPolicy.KEEP, work, Collections.singletonList(this));
    }

    @Override
    public @NonNull
    LiveData<List<WorkInfo>> getWorkInfosLiveData() {
        return mWorkManagerImpl.getWorkInfosById(mAllIds);
    }

    @Override
    public @NonNull void enqueue() {
        if (!mEnqueued) {
            EnqueueRunnable runnable = new EnqueueRunnable(this);
            mWorkManagerImpl.getProcessor().getWorkTaskExecutor().diskIO().execute (runnable);
        }
    }

    protected @NonNull
    WorkContinuation combineInternal(
            @NonNull List<WorkContinuation> continuations) {
        OneTimeWorkRequest combinedWork = new OneTimeWorkRequest.Builder(CombineContinuationsWorker.class).build();

        List<WorkContinuationImpl> parents = new ArrayList<>(continuations.size());
        for (WorkContinuation continuation : continuations) {
            parents.add((WorkContinuationImpl) continuation);
        }

        return new WorkContinuationImpl(mWorkManagerImpl, null,
                ExistingWorkPolicy.KEEP, Collections.singletonList(combinedWork), parents);
    }

    public boolean hasCycles() {
        return hasCycles(this, new HashSet<String>());
    }

    private static boolean hasCycles(
            @NonNull WorkContinuationImpl continuation,
            @NonNull Set<String> visited) {

        visited.addAll(continuation.getIds());
        Set<String> prerequisiteIds = prerequisitesFor(continuation);
        for (String id : visited) {
            if (prerequisiteIds.contains(id)) {
                return true;
            }
        }

        List<WorkContinuationImpl> parents = continuation.getParents();
        if (parents != null && !parents.isEmpty()) {
            for (WorkContinuationImpl parent : parents) {
                if (hasCycles(parent, visited)) {
                    return true;
                }
            }
        }

        visited.removeAll(continuation.getIds());
        return false;
    }

    public static Set<String> prerequisitesFor(WorkContinuationImpl continuation) {
        Set<String> prerequisites = new HashSet<>();
        List<WorkContinuationImpl> parents = continuation.getParents();
        if (parents != null && !parents.isEmpty()) {
            for (WorkContinuationImpl parent : parents) {
                prerequisites.addAll(parent.getIds());
            }
        }
        return prerequisites;
    }
}
