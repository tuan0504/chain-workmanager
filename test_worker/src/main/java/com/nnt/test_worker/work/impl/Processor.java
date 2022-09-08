package com.nnt.test_worker.work.impl;

import android.content.Context;

import com.nnt.test_worker.work.impl.runnable.WorkerWrapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Processor {
    private final Object mLock;

    private final Context mAppContext;
    private final WorkDatabase mWorkDatabase;
    private final Map<String, WorkerWrapper> mEnqueuedWorkMap;
    private final Set<String> mCancelledIds;
    private final AppExecutors mWorkTaskExecutor = AppExecutors.getInstance();

    public Processor(
            Context appContext,
            WorkDatabase workDatabase) {
        mAppContext = appContext;
        mWorkDatabase = workDatabase;
        mEnqueuedWorkMap = new HashMap<>();
        mCancelledIds = new HashSet<>();
        mLock = new Object();
    }

    public AppExecutors getWorkTaskExecutor() {
        return mWorkTaskExecutor;
    }

    public void executeBackground(Runnable runnable) {
        mWorkTaskExecutor.diskIO().execute(runnable);
    }

    public boolean startWork(String id) {
        WorkerWrapper workWrapper;
        synchronized (mLock) {
            if (mEnqueuedWorkMap.containsKey(id)) {
                return false;
            }
            workWrapper = new WorkerWrapper.Builder(mAppContext, mWorkDatabase, id).build();
            mEnqueuedWorkMap.put(id, workWrapper);
        }
        mWorkTaskExecutor.networkIO().execute(workWrapper);
        return true;
    }

    public boolean stopWork(String id) {
        synchronized (mLock) {
            WorkerWrapper wrapper = mEnqueuedWorkMap.remove(id);
            if (wrapper != null) {
                wrapper.interrupt(false);
                return true;
            }
            return false;
        }
    }

    public boolean stopAndCancelWork(String id) {
        synchronized (mLock) {
            mCancelledIds.add(id);
            WorkerWrapper wrapper = mEnqueuedWorkMap.remove(id);
            if (wrapper != null) {
                wrapper.interrupt(true);
                return true;
            }
            return false;
        }
    }

    public boolean isCancelled(String id) {
        synchronized (mLock) {
            return mCancelledIds.contains(id);
        }
    }

    public boolean hasWork() {
        synchronized (mLock) {
            return !mEnqueuedWorkMap.isEmpty();
        }
    }

    public boolean isEnqueued(String workSpecId) {
        synchronized (mLock) {
            return mEnqueuedWorkMap.containsKey(workSpecId);
        }
    }
}
