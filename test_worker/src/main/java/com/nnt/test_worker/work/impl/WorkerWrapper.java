package com.nnt.test_worker.work.impl;

import static com.nnt.test_worker.work.WorkInfo.State.BLOCKED;
import static com.nnt.test_worker.work.WorkInfo.State.CANCELLED;
import static com.nnt.test_worker.work.WorkInfo.State.ENQUEUED;
import static com.nnt.test_worker.work.WorkInfo.State.FAILED;
import static com.nnt.test_worker.work.WorkInfo.State.RUNNING;
import static com.nnt.test_worker.work.WorkInfo.State.SUCCEEDED;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.nnt.test_worker.work.Data;
import com.nnt.test_worker.work.InputMerger;
import com.nnt.test_worker.work.ListenableWorker;
import com.nnt.test_worker.work.WorkInfo;
import com.nnt.test_worker.work.Worker;
import com.nnt.test_worker.work.WorkerParameters;
import com.nnt.test_worker.work.impl.inputmerge.OverwritingInputMerger;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WorkerWrapper implements Runnable {

    private final Context mAppContext;
    private final String mWorkSpecId;
    WorkSpec mWorkSpec;
    private final WorkDatabase mWorkDatabase;
    private volatile boolean mInterrupted;
    Worker mWorker;
    private List<String> mTags;

    WorkerWrapper(Builder builder) {
        mAppContext = builder.mAppContext;
        mWorkSpecId = builder.mWorkSpecId;
        mWorkDatabase = builder.mWorkDatabase;
    }

    @WorkerThread
    @Override
    public void run() {
        mTags = mWorkDatabase.getTagsByWorkId(mWorkSpecId);
        runWorker();
    }

    private void runWorker() {
        if (tryCheckForInterruptionAndResolve()) {
            return;
        }

        mWorkSpec = mWorkDatabase.getWorkSpec(mWorkSpecId);
        if (mWorkSpec == null) {
            resolve(false);
            return;
        }

        if (mWorkSpec.state != ENQUEUED) {
            resolveIncorrectStatus();
            return;
        }

        Data input = mWorkSpec.input;
        if (mWorker == null) {
            InputMerger inputMerger = new OverwritingInputMerger();
            List<Data> inputs = new ArrayList<>();
            inputs.add(mWorkSpec.input);
            inputs.addAll(mWorkDatabase.getInputsFromPrerequisites(mWorkSpecId));
            input = inputMerger.merge(inputs);
        }

        WorkerParameters params = new WorkerParameters(
                UUID.fromString(mWorkSpecId), input, mTags,
                mWorkSpec.runAttemptCount);

        mWorker = createWorker(mWorkSpec.workerClassName, params);

        if (mWorker.isUsed()) {
            setFailedAndResolve(new ListenableWorker.Result.Failure());
            return;
        }
        mWorker.setUsed();

        if (trySetRunning()) {
            if (tryCheckForInterruptionAndResolve()) {
                return;
            }

            ListenableWorker.Result result = mWorker.doWork();
            handleResult(result);
        } else {
            resolveIncorrectStatus();
        }
    }
    private Worker createWorker(String workerClassName, WorkerParameters parameters) {
        Worker worker;
        Class<? extends Worker> clazz;
        try {
            clazz = Class.forName(workerClassName).asSubclass(Worker.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        try {
            Constructor<? extends Worker> constructor = clazz.getDeclaredConstructor(Context.class, WorkerParameters.class);
            worker = constructor.newInstance(mAppContext, parameters);
            return worker;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void interrupt(boolean cancelled) {
        mInterrupted = true;
        tryCheckForInterruptionAndResolve();
    }

    private void resolveIncorrectStatus() {
        WorkInfo.State state = mWorkDatabase.getState(mWorkSpecId);
        resolve(state == RUNNING);
    }

    private boolean tryCheckForInterruptionAndResolve() {
        if (mInterrupted) {
            WorkInfo.State state = mWorkDatabase.getState(mWorkSpecId);
            resolve(state != null && !state.isFinished());
            return true;
        }
        return false;
    }

    private void handleResult(ListenableWorker.Result result) {
        Log.e("TUAN", "handleResult :" + result);
        if(result instanceof ListenableWorker.Result.Success) {
            setSucceededAndResolve((ListenableWorker.Result.Success)result);
        } else if (result instanceof ListenableWorker.Result.Failure) {
            setFailedAndResolve((ListenableWorker.Result.Failure)result);
        } else if (result instanceof ListenableWorker.Result.Retry) {
            rescheduleAndResolve();
        }
    }

    private boolean trySetRunning() {
        boolean setToRunning = false;

        WorkInfo.State currentState = mWorkDatabase.getState(mWorkSpecId);
        if (currentState == ENQUEUED) {
            mWorkDatabase.setState(RUNNING, mWorkSpecId);
            mWorkDatabase.incrementWorkSpecRunAttemptCount(mWorkSpecId);
            setToRunning = true;
        }
        return setToRunning;
    }

    void setFailedAndResolve(ListenableWorker.Result.Failure failure) {
        ArrayList<String> ids = new ArrayList<>();
        ids.add(mWorkSpecId);
        while (!ids.isEmpty()) {
            String id = ids.remove(0);
            if(mWorkDatabase.getState(id) != CANCELLED) {
                mWorkDatabase.setState(FAILED, id);
            }
            ids.addAll(mWorkDatabase.getDependentWorkIds(id));
        }
        mWorkDatabase.setWorkSpecOutput(mWorkSpecId, failure.getOutputData());
        resolve(false);
    }

    private void rescheduleAndResolve() {
        mWorkDatabase.setState(ENQUEUED, mWorkSpecId);
        mWorker.retry();
        runWorker();
        resolve(true);
    }

    private void setSucceededAndResolve(ListenableWorker.Result.Success success) {
        mWorkDatabase.setState(SUCCEEDED, mWorkSpecId);
        mWorkDatabase.setWorkSpecOutput(mWorkSpecId, success.getOutputData());

        List<String> dependentWorkIds = mWorkDatabase.getDependentWorkIds(mWorkSpecId);
        for(String id : dependentWorkIds) {
            if(mWorkDatabase.getState(id) == BLOCKED && mWorkDatabase.hasCompletedAllPrerequisites(id)) {
                mWorkDatabase.setState(ENQUEUED, id);
            }
        }
        resolve(false);
    }

    private void resolve(final boolean needsReschedule) {
        List<String> listIds = mWorkDatabase.getAllUnfinishedWork();
        if(!listIds.isEmpty()) {
            for(String id : listIds) {
                if(mWorkDatabase.getState(id) == ENQUEUED ) {
                    WorkManagerImpl.getInstance().startWork(id);
                }
            }
        }
    }

    public static class Builder {

        @NonNull Context mAppContext;
        @NonNull WorkDatabase mWorkDatabase;
        @NonNull String mWorkSpecId;

        public Builder(@NonNull Context context,
                @NonNull WorkDatabase database,
                @NonNull String workSpecId) {
            mAppContext = context.getApplicationContext();
            mWorkDatabase = database;
            mWorkSpecId = workSpecId;
        }

        public WorkerWrapper build() {
            return new WorkerWrapper(this);
        }
    }
}
