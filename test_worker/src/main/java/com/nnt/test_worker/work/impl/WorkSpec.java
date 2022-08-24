package com.nnt.test_worker.work.impl;

import static com.nnt.test_worker.work.WorkInfo.State.ENQUEUED;

import android.support.annotation.NonNull;

import com.nnt.test_worker.work.Data;
import com.nnt.test_worker.work.WorkInfo;

public class WorkSpec {

    public String id;

    public WorkInfo.State state = ENQUEUED;

    public String workerClassName;

    public Data input = Data.EMPTY;

    public Data output = Data.EMPTY;

    public int runAttemptCount;

    public WorkSpec(@NonNull String id, @NonNull String workerClassName) {
        this.id = id;
        this.workerClassName = workerClassName;
    }

    public WorkSpec(@NonNull WorkSpec other) {
        id = other.id;
        workerClassName = other.workerClassName;
        state = other.state;
        input = new Data(other.input);
        output = new Data(other.output);
        runAttemptCount = other.runAttemptCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkSpec workSpec = (WorkSpec) o;

        if (runAttemptCount != workSpec.runAttemptCount) return false;
        if (!id.equals(workSpec.id)) return false;
        if (state != workSpec.state) return false;
        if (!workerClassName.equals(workSpec.workerClassName)) return false;
        if (!input.equals(workSpec.input)) return false;
        return output.equals(workSpec.output);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + state.hashCode();
        result = 31 * result + workerClassName.hashCode();
        result = 31 * result + input.hashCode();
        result = 31 * result + output.hashCode();
        result = 31 * result + runAttemptCount;
        return result;
    }

    @Override
    public String toString() {
        return "{WorkSpec: " + id + "}";
    }
}
