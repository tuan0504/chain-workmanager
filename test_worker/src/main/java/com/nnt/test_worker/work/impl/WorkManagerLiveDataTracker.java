package com.nnt.test_worker.work.impl;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.VisibleForTesting;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

class WorkManagerLiveDataTracker {
    @VisibleForTesting
    final Set<LiveData> mLiveDataSet = Collections.newSetFromMap(new IdentityHashMap<>());

    public <T> LiveData<T> track(LiveData<T> other) {
        return new TrackedLiveData<>(this, other);
    }

    void onActive(LiveData liveData) {
        mLiveDataSet.add(liveData);
    }

    void onInactive(LiveData liveData) {
        mLiveDataSet.remove(liveData);
    }

    static class TrackedLiveData<T> extends MediatorLiveData<T> {
        private final WorkManagerLiveDataTracker mContainer;
        TrackedLiveData(WorkManagerLiveDataTracker container, LiveData<T> wrapped) {
            mContainer = container;
            addSource(wrapped, t -> setValue(t));
        }

        @Override
        protected void onActive() {
            super.onActive();
            mContainer.onActive(this);
        }


        @Override
        protected void onInactive() {
            super.onInactive();
            mContainer.onInactive(this);
        }
    }
}
