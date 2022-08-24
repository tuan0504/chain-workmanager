package com.nnt.test_worker.work;

import android.support.annotation.NonNull;

import com.nnt.test_worker.work.impl.WorkSpec;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class WorkRequest {

    private @NonNull UUID mId;
    private @NonNull WorkSpec mWorkSpec;
    private @NonNull Set<String> mTags;

    protected WorkRequest(@NonNull UUID id, @NonNull WorkSpec workSpec, @NonNull Set<String> tags) {
        mId = id;
        mWorkSpec = workSpec;
        mTags = tags;
    }

    public @NonNull UUID getId() {
        return mId;
    }

    public @NonNull String getStringId() {
        return mId.toString();
    }

    public @NonNull WorkSpec getWorkSpec() {
        return mWorkSpec;
    }

    public @NonNull Set<String> getTags() {
        return mTags;
    }

    public abstract static class Builder<B extends Builder, W extends WorkRequest> {

        UUID mId;
        WorkSpec mWorkSpec;
        Set<String> mTags = new HashSet<>();

        Builder(@NonNull Class<? extends ListenableWorker> workerClass) {
            mId = UUID.randomUUID();
            mWorkSpec = new WorkSpec(mId.toString(), workerClass.getName());
            addTag(workerClass.getName());
        }

        public final @NonNull B setInputData(@NonNull Data inputData) {
            mWorkSpec.input = inputData;
            return getThis();
        }

        public final @NonNull B addTag(@NonNull String tag) {
            mTags.add(tag);
            return getThis();
        }

        public final @NonNull W build() {
            W returnValue = buildInternal();
            // Create a new id and WorkSpec so this WorkRequest.Builder can be used multiple times.
            mId = UUID.randomUUID();
            mWorkSpec = new WorkSpec(mWorkSpec);
            mWorkSpec.id = mId.toString();
            return returnValue;
        }

        abstract @NonNull W buildInternal();

        abstract @NonNull B getThis();
    }
}
