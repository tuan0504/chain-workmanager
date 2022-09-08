package com.nnt.test_worker.work;

import android.content.Context;

import com.nnt.test_worker.work.datatypes.Data;
import com.nnt.test_worker.work.datatypes.WorkerParameters;

import java.util.Set;
import java.util.UUID;

public abstract class Worker {

    private final Context mAppContext;
    private final
    WorkerParameters mWorkerParams;

    private volatile boolean mStopped;

    private boolean mUsed;

    public Worker(Context context, WorkerParameters workerParams) {
        mAppContext = context.getApplicationContext();
        mWorkerParams = workerParams;
    }


    public abstract Result doWork();

    public final Context getApplicationContext() {
        return mAppContext;
    }

    public final UUID getId() {
        return mWorkerParams.getId();
    }

    public final Data getInputData() {
        return mWorkerParams.getInputData();
    }

    public final Set<String> getTags() {
        return mWorkerParams.getTags();
    }

    public final int getRunAttemptCount() {
        return mWorkerParams.getRunAttemptCount();
    }

    public final boolean isStopped() {
        return mStopped;
    }

    public final void stop() {
        mStopped = true;
        onStopped();
    }

    public void onStopped() {
        // Do nothing by default.
    }

    public final boolean isUsed() {
        return mUsed;
    }

    public final void setUsed() {
        mUsed = true;
    }

    public abstract static class Result {
        protected Data mOutputData = Data.EMPTY;

        public static Result success() {
            return new Result.Success();
        }

        public static Result success(Data outputData) {
            return new Result.Success(outputData);
        }

        public static Result retry() {
            return new Result.Retry();
        }

        public static Result failure() {
            return new Result.Failure();
        }

        public static Result failure(Data outputData) {
            return new Result.Failure(outputData);
        }

        public Data getOutputData() {
            return mOutputData;
        }

        protected Result() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return mOutputData.equals(((Result) o).getOutputData());
        }

        @Override
        public int hashCode() {
            String name = this.getClass().getName();
            return 31 * name.hashCode() + mOutputData.hashCode();
        }

        public static final class Success extends Result {

            public Success() {
                this(Data.EMPTY);
            }

            public Success(Data outputData) {
                super();
                mOutputData = outputData;
            }

            @Override
            public String toString() {
                return "Success";
            }
        }

        public static final class Failure extends Result {

            public Failure() {
                this(Data.EMPTY);
            }

            public Failure(Data outputData) {
                super();
                mOutputData = outputData;
            }

            @Override
            public String toString() {
                return "Failure";
            }
        }

        public static final class Retry extends Result {
            public Retry() {
                super();
            }

            @Override
            public String toString() {
                return "Retry";
            }
        }
    }
}
