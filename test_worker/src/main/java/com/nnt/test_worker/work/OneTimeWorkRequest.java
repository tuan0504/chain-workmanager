package com.nnt.test_worker.work;

import android.support.annotation.NonNull;

public final class OneTimeWorkRequest extends WorkRequest {

    OneTimeWorkRequest(Builder builder) {
        super(builder.mId, builder.mWorkSpec, builder.mTags);
    }

    public static final class Builder extends WorkRequest.Builder<Builder, OneTimeWorkRequest> {

        public Builder(@NonNull Class<? extends ListenableWorker> workerClass) {
            super(workerClass);
        }

        @Override
        @NonNull OneTimeWorkRequest buildInternal() {
            return new OneTimeWorkRequest(this);
        }

        @Override
        @NonNull Builder getThis() {
            return this;
        }
    }
}
