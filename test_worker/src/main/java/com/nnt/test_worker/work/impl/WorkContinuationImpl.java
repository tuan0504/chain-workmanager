package com.nnt.test_worker.work.impl;

import com.nnt.test_worker.work.CombineContinuationsWorker;
import com.nnt.test_worker.work.OneTimeWorkRequest;
import com.nnt.test_worker.work.WorkContinuation;
import com.nnt.test_worker.work.WorkRequest;
import com.nnt.test_worker.work.datatypes.ExistingWorkPolicy;
import com.nnt.test_worker.work.datatypes.WorkInfo;
import com.nnt.test_worker.work.impl.runnable.EnqueueRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorkContinuationImpl extends WorkContinuation {

    private final WorkManagerImpl mWorkManagerImpl;
    private final String mName;
    private final ExistingWorkPolicy mExistingWorkPolicy;
    private final List<? extends WorkRequest> mWork;
    private final List<String> mIds;
    private final List<String> mAllIds;
    private final List<WorkContinuationImpl> mParents;

    private boolean mEnqueued;


    public WorkManagerImpl getWorkManagerImpl() {
        return mWorkManagerImpl;
    }


    public String getName() {
        return mName;
    }

    public ExistingWorkPolicy getExistingWorkPolicy() {
        return mExistingWorkPolicy;
    }


    public List<? extends WorkRequest> getWork() {
        return mWork;
    }


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
            WorkManagerImpl workManagerImpl,
            List<? extends WorkRequest> work) {
        this(
                workManagerImpl,
                null,
                ExistingWorkPolicy.KEEP,
                work,
                null);
    }

    WorkContinuationImpl(
            WorkManagerImpl workManagerImpl,
            String name,
            ExistingWorkPolicy existingWorkPolicy,
            List<? extends WorkRequest> work) {
        this(workManagerImpl, name, existingWorkPolicy, work, null);
    }

    WorkContinuationImpl(WorkManagerImpl workManagerImpl,
                         String name,
                         ExistingWorkPolicy existingWorkPolicy,
                         List<? extends WorkRequest> work,
                         List<WorkContinuationImpl> parents) {
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
    public WorkContinuation then(List<OneTimeWorkRequest> work) {
        return new WorkContinuationImpl(mWorkManagerImpl, mName, mExistingWorkPolicy, work, Collections.singletonList(this));
    }

    @Override
    public void enqueue() {
        if (!mEnqueued) {
            EnqueueRunnable runnable = new EnqueueRunnable(this);
            mWorkManagerImpl.getProcessor().executeBackground(runnable);
        }
    }

    protected WorkContinuation combineInternal(
            List<WorkContinuation> continuations) {
        OneTimeWorkRequest combinedWork = new OneTimeWorkRequest.Builder(CombineContinuationsWorker.class).build();

        List<WorkContinuationImpl> parents = new ArrayList<>(continuations.size());
        for (WorkContinuation continuation : continuations) {
            parents.add((WorkContinuationImpl) continuation);
        }

        return new WorkContinuationImpl(mWorkManagerImpl, null,
                ExistingWorkPolicy.KEEP, Collections.singletonList(combinedWork), parents);
    }

    @Override
    public WorkDatabase.ObservableItem<List<WorkInfo>> getWorkInfosLiveData() {
        return mWorkManagerImpl.getWorkInfosByIDs(mAllIds);
    }

    public boolean hasCycles() {
        return hasCycles(this, new HashSet<String>());
    }

    private static boolean hasCycles(
            WorkContinuationImpl continuation,
            Set<String> visited) {

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
