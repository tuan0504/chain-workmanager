package com.nnt.test_worker.work.impl.runnable;

import static com.nnt.test_worker.work.datatypes.WorkInfo.State.BLOCKED;
import static com.nnt.test_worker.work.datatypes.WorkInfo.State.CANCELLED;
import static com.nnt.test_worker.work.datatypes.WorkInfo.State.ENQUEUED;
import static com.nnt.test_worker.work.datatypes.WorkInfo.State.FAILED;
import static com.nnt.test_worker.work.datatypes.WorkInfo.State.RUNNING;
import static com.nnt.test_worker.work.datatypes.WorkInfo.State.SUCCEEDED;

import android.content.Context;

import com.nnt.test_worker.work.Worker;
import com.nnt.test_worker.work.Worker.Result;
import com.nnt.test_worker.work.datatypes.Data;
import com.nnt.test_worker.work.datatypes.WorkInfo;
import com.nnt.test_worker.work.datatypes.WorkSpec;
import com.nnt.test_worker.work.datatypes.WorkerParameters;
import com.nnt.test_worker.work.impl.WorkDatabase;
import com.nnt.test_worker.work.impl.WorkManagerImpl;
import com.nnt.test_worker.work.inputmerge.InputMerger;
import com.nnt.test_worker.work.inputmerge.OverwritingInputMerger;

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
    private final List<String> mTags;
    private int runAttemptCount = 0;

    WorkerWrapper(Builder builder) {
        mAppContext = builder.mAppContext;
        mWorkSpecId = builder.mWorkSpecId;
        mWorkDatabase = builder.mWorkDatabase;
        mTags = mWorkDatabase.getTagsByWorkId(mWorkSpecId);
    }


    @Override
    public void run() {
        runWorker();
    }

    private void runWorker() {
        if (mInterrupted) {
            return;
        }

        mWorkSpec = mWorkDatabase.getWorkSpec(mWorkSpecId);
        if (mWorkSpec == null || mWorkSpec.state != ENQUEUED) {
            return;
        }

        if (mWorker == null) {
            InputMerger inputMerger = new OverwritingInputMerger();
            List<Data> inputs = new ArrayList<>();
            inputs.add(mWorkSpec.input);
            inputs.addAll(mWorkDatabase.getInputsFromPrerequisites(mWorkSpecId));
            Data input = inputMerger.merge(inputs);
            WorkerParameters params = new WorkerParameters(
                    UUID.fromString(mWorkSpecId), input, mTags, runAttemptCount);

            mWorker = createWorker(mWorkSpec.workerClassName, params);
        }

        if (mWorker == null || mWorker.isUsed()) {
            setFailedResult(new Result.Failure());
            return;
        }

        mWorker.setUsed();

        if (trySetRunning()) {
            if (mInterrupted) {
                return;
            }

            Result result = mWorker.doWork();
            handleResult(result);
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
    }

    private void handleResult(Result result) {
        if (result instanceof Result.Success) {
            setSucceededResult((Result.Success) result);
        } else if (result instanceof Result.Failure) {
            setFailedResult((Result.Failure) result);
        } else if (result instanceof Result.Retry) {
            reschedule();
        }
    }

    private boolean trySetRunning() {
        boolean setToRunning = false;

        WorkInfo.State currentState = mWorkDatabase.getState(mWorkSpecId);
        if (currentState == ENQUEUED) {
            mWorkDatabase.setState(RUNNING, mWorkSpecId);
            runAttemptCount++;
            setToRunning = true;
        }
        return setToRunning;
    }

    void setFailedResult(Result.Failure failure) {
        ArrayList<String> ids = new ArrayList<>();
        ids.add(mWorkSpecId);
        while (!ids.isEmpty()) {
            String id = ids.remove(0);
            if (mWorkDatabase.getState(id) != CANCELLED) {
                mWorkDatabase.setState(FAILED, id);
            }
            ids.addAll(mWorkDatabase.getDependentWorkIds(id));
        }
        mWorkDatabase.setWorkSpecOutput(mWorkSpecId, failure.getOutputData());
    }

    private void reschedule() {
        mWorkDatabase.setState(ENQUEUED, mWorkSpecId);
        WorkerParameters parameters = new WorkerParameters(UUID.fromString(mWorkSpecId),
                mWorker.getInputData(), mTags, runAttemptCount);

        mWorker = createWorker(mWorkSpec.workerClassName, parameters);
        runWorker();
    }

    private void setSucceededResult(Result.Success success) {
        mWorkDatabase.setState(SUCCEEDED, mWorkSpecId);
        mWorkDatabase.setWorkSpecOutput(mWorkSpecId, success.getOutputData());

        List<String> dependentWorkIds = mWorkDatabase.getDependentWorkIds(mWorkSpecId);
        for (String id : dependentWorkIds) {
            if (mWorkDatabase.getState(id) == BLOCKED && mWorkDatabase.hasCompletedAllPrerequisites(id)) {
                mWorkDatabase.setState(ENQUEUED, id);
                WorkManagerImpl.getInstance().startWork(id);
            }
        }
    }

    public static class Builder {

        Context mAppContext;

        WorkDatabase mWorkDatabase;

        String mWorkSpecId;

        public Builder(Context context,
                       WorkDatabase database,
                       String workSpecId) {
            mAppContext = context.getApplicationContext();
            mWorkDatabase = database;
            mWorkSpecId = workSpecId;
        }

        public WorkerWrapper build() {
            return new WorkerWrapper(this);
        }
    }
}
