package com.nnt.test_worker.work;

public final class OneTimeWorkRequest extends WorkRequest {

    OneTimeWorkRequest(Builder builder) {
        super(builder.mId, builder.mWorkSpec, builder.mTags);
    }

    public static final class Builder extends WorkRequest.Builder<Builder, OneTimeWorkRequest> {

        public Builder(Class<? extends Worker> workerClass) {
            super(workerClass);
        }

        @Override
        OneTimeWorkRequest buildInternal() {
            return new OneTimeWorkRequest(this);
        }

        @Override
        Builder getThis() {
            return this;
        }
    }
}
