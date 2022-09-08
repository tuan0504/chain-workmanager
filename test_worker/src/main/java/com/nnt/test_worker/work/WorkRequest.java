package com.nnt.test_worker.work;

import com.nnt.test_worker.work.datatypes.Data;
import com.nnt.test_worker.work.datatypes.WorkSpec;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class WorkRequest {

    private final UUID mId;
    private final WorkSpec mWorkSpec;
    private final Set<String> mTags;

    protected WorkRequest(UUID id, WorkSpec workSpec, Set<String> tags) {
        mId = id;
        mWorkSpec = workSpec;
        mTags = tags;
    }

    public UUID getId() {
        return mId;
    }

    public String getStringId() {
        return mId.toString();
    }

    public WorkSpec getWorkSpec() {
        return mWorkSpec;
    }

    public Set<String> getTags() {
        return mTags;
    }

    public abstract static class Builder<B extends Builder, W extends WorkRequest> {

        UUID mId;
        WorkSpec mWorkSpec;
        Set<String> mTags = new HashSet<>();

        Builder(Class<? extends Worker> workerClass) {
            mId = UUID.randomUUID();
            mWorkSpec = new WorkSpec(mId.toString(), workerClass.getName());
            addTag(workerClass.getName());
        }

        public final B setInputData(Data inputData) {
            mWorkSpec.input = inputData;
            return getThis();
        }

        public final B addTag(String tag) {
            mTags.add(tag);
            return getThis();
        }

        public final W build() {
            W returnValue = buildInternal();
            // Create a new id and WorkSpec so this WorkRequest.Builder can be used multiple times.
            mId = UUID.randomUUID();
            mWorkSpec = new WorkSpec(mWorkSpec);
            mWorkSpec.id = mId.toString();
            return returnValue;
        }

        abstract W buildInternal();

        abstract B getThis();
    }
}
