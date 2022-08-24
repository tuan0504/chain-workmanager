/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nnt.test_worker.work;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Set;
import java.util.UUID;

public abstract class ListenableWorker {

    private @NonNull Context mAppContext;
    private @NonNull WorkerParameters mWorkerParams;

    private volatile boolean mStopped;

    private boolean mUsed;

    public ListenableWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        mAppContext = appContext;
        mWorkerParams = workerParams;
    }

    public final @NonNull Context getApplicationContext() {
        return mAppContext;
    }

    public final @NonNull UUID getId() {
        return mWorkerParams.getId();
    }

    public final @NonNull Data getInputData() {
        return mWorkerParams.getInputData();
    }

    public final @NonNull Set<String> getTags() {
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
    public final void retry() {
        mUsed = false;
    }

    public abstract static class Result {
        protected Data mOutputData = Data.EMPTY;

        public static Result success() {
            return new Success();
        }

        public static Result success(@NonNull Data outputData) {
            return new Success(outputData);
        }

        public static Result retry() {
            return new Retry();
        }

        public static Result failure() {
            return new Failure();
        }

        public static Result failure(@NonNull Data outputData) {
            return new Failure(outputData);
        }

        public Data getOutputData() {return mOutputData;}

        protected Result() {}

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

            public Success(@NonNull Data outputData) {
                super();
                mOutputData = outputData;
            }

            @Override
            public String toString() {
                return "Success";
            }
        }

        public static final class Failure extends Result {
            private final Data mOutputData;

            public Failure() {
                this(Data.EMPTY);
            }

            public Failure(@NonNull Data outputData) {
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
