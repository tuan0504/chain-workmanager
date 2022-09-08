package com.nnt.test_worker.work.datatypes;

import static com.nnt.test_worker.work.datatypes.WorkInfo.State.ENQUEUED;

public class WorkSpec {

    public String id;

    public WorkInfo.State state = ENQUEUED;

    public String workerClassName;

    public Data input = Data.EMPTY;

    public Data output = Data.EMPTY;

    public WorkSpec(String id, String workerClassName) {
        this.id = id;
        this.workerClassName = workerClassName;
    }

    public WorkSpec(WorkSpec other) {
        id = other.id;
        workerClassName = other.workerClassName;
        state = other.state;
        input = new Data(other.input);
        output = new Data(other.output);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkSpec workSpec = (WorkSpec) o;

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
        return result;
    }

    @Override
    public String toString() {
        return "{WorkSpec: " + id + "}";
    }
}
