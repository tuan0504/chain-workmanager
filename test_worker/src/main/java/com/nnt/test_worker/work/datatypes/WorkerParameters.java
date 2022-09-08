package com.nnt.test_worker.work.datatypes;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class WorkerParameters {

    private final UUID mId;
    private final
    Data mInputData;
    private final Set<String> mTags;
    private final int mRunAttemptCount;

    public WorkerParameters(
            UUID id,
            Data inputData,
            Collection<String> tags,
            int runAttemptCount) {
        mId = id;
        mInputData = inputData;
        mTags = new HashSet<>(tags);
        mRunAttemptCount = runAttemptCount;
    }

    public UUID getId() {
        return mId;
    }

    public Data getInputData() {
        return mInputData;
    }

    public Set<String> getTags() {
        return mTags;
    }

    public int getRunAttemptCount() {
        return mRunAttemptCount;
    }
}
