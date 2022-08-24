package com.nnt.test_worker.work;

import android.support.annotation.NonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class WorkInfo {

    private @NonNull UUID mId;
    private @NonNull State mState;
    private @NonNull Data mOutputData;
    private @NonNull Set<String> mTags;

    public WorkInfo(
            @NonNull UUID id,
            @NonNull State state,
            @NonNull Data outputData,
            @NonNull List<String> tags) {
        mId = id;
        mState = state;
        mOutputData = outputData;
        mTags = new HashSet<>(tags);
    }

    public @NonNull UUID getId() {
        return mId;
    }

    public @NonNull State getState() {
        return mState;
    }

    public @NonNull Data getOutputData() {
        return mOutputData;
    }

    public @NonNull Set<String> getTags() {
        return mTags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkInfo that = (WorkInfo) o;

        if (!Objects.equals(mId, that.mId)) return false;
        if (mState != that.mState) return false;
        if (!Objects.equals(mOutputData, that.mOutputData)) {
            return false;
        }
        return Objects.equals(mTags, that.mTags);
    }

    @Override
    public int hashCode() {
        int result = mId.hashCode();
        result = 31 * result + mState.hashCode();
        result = 31 * result + mOutputData.hashCode();
        result = 31 * result + mTags.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "WorkInfo{"
                +   "mId='" + mId + '\''
                +   ", mState=" + mState
                +   ", mOutputData=" + mOutputData
                +   ", mTags=" + mTags
                + '}';
    }

    public enum State {

        ENQUEUED,

        RUNNING,

        SUCCEEDED,

        FAILED,

        BLOCKED,

        CANCELLED;

        public boolean isFinished() {
            return (this == SUCCEEDED || this == FAILED || this == CANCELLED);
        }
    }
}
