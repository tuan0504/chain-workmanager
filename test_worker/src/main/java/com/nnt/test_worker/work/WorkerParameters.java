package com.nnt.test_worker.work;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class WorkerParameters {

    private @NonNull UUID mId;
    private @NonNull Data mInputData;
    private @NonNull Set<String> mTags;
    private int mRunAttemptCount;

    public WorkerParameters(
            @NonNull UUID id,
            @NonNull Data inputData,
            @NonNull Collection<String> tags,
            int runAttemptCount) {
        mId = id;
        mInputData = inputData;
        mTags = new HashSet<>(tags);
        mRunAttemptCount = runAttemptCount;
    }

    public @NonNull UUID getId() {
        return mId;
    }

    public @NonNull Data getInputData() {
        return mInputData;
    }

    public @NonNull Set<String> getTags() {
        return mTags;
    }

    public int getRunAttemptCount() {
        return mRunAttemptCount;
    }
}
