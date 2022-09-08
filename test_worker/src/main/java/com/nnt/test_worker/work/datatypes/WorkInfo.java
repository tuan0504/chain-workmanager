package com.nnt.test_worker.work.datatypes;

import java.util.Objects;
import java.util.UUID;

public final class WorkInfo {

    private final UUID mId;
    private final State mState;
    private final Data mOutputData;

    public WorkInfo(
            UUID id,
            State state,
            Data outputData) {
        mId = id;
        mState = state;
        mOutputData = outputData;
    }

    public UUID getId() {
        return mId;
    }

    public State getState() {
        return mState;
    }

    public Data getOutputData() {
        return mOutputData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkInfo that = (WorkInfo) o;

        if (!Objects.equals(mId, that.mId)) return false;
        if (mState != that.mState) return false;
        return Objects.equals(mOutputData, that.mOutputData);
    }

    @Override
    public int hashCode() {
        int result = mId.hashCode();
        result = 31 * result + mState.hashCode();
        result = 31 * result + mOutputData.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "WorkInfo{"
                + "mId='" + mId + '\''
                + ", mState=" + mState
                + ", mOutputData=" + mOutputData
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
