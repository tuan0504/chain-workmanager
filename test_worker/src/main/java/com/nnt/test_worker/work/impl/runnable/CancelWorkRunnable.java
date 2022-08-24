package com.nnt.test_worker.work.impl.runnable;

import static com.nnt.test_worker.work.WorkInfo.State.CANCELLED;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.nnt.test_worker.work.WorkInfo;
import com.nnt.test_worker.work.impl.Processor;
import com.nnt.test_worker.work.impl.WorkDatabase;
import com.nnt.test_worker.work.impl.WorkManagerImpl;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public abstract class CancelWorkRunnable implements Runnable {
    @Override
    public void run() {
        try {
            runInternal();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    abstract void runInternal();

    void cancel(WorkManagerImpl workManagerImpl, String workSpecId) {
        iterativelyCancelWorkAndDependents(workManagerImpl.getWorkDatabase(), workSpecId);

        Processor processor = workManagerImpl.getProcessor();
        processor.stopAndCancelWork(workSpecId);
    }

    private void iterativelyCancelWorkAndDependents(WorkDatabase workDatabase, String workSpecId) {
        LinkedList<String> idsToProcess = new LinkedList<>();
        idsToProcess.add(workSpecId);
        while (!idsToProcess.isEmpty()) {
            String id = idsToProcess.remove();
            WorkInfo.State state = workDatabase.getState(id);
            if (!state.isFinished()) {
                workDatabase.setState(CANCELLED, id);
            }
            idsToProcess.addAll(workDatabase.getDependentWorkIds(id));
        }
    }

    public static CancelWorkRunnable forId(@NonNull final UUID id, @NonNull final WorkManagerImpl workManagerImpl) {
        return new CancelWorkRunnable() {
            @WorkerThread
            @Override
            void runInternal() {
                Log.e("TUAN", "CancelWork " + id);
                cancel(workManagerImpl, id.toString());
            }
        };
    }

    public static CancelWorkRunnable forTag(@NonNull final String tag, @NonNull final WorkManagerImpl workManagerImpl) {
        return new CancelWorkRunnable() {
            @WorkerThread
            @Override
            void runInternal() {
                Log.e("TUAN", "CancelWork " + tag);
                WorkDatabase workDatabase = workManagerImpl.getWorkDatabase();
                List<String> workSpecIds = workDatabase.getWorkIdByTag(tag);
                for (String workSpecId : workSpecIds) {
                    cancel(workManagerImpl, workSpecId);
                }
            }
        };
    }

    public static CancelWorkRunnable forName(
            @NonNull final String name,
            @NonNull final WorkManagerImpl workManagerImpl) {
        return new CancelWorkRunnable() {
            @WorkerThread
            @Override
            void runInternal() {
                Log.e("TUAN", "CancelWork " + name);
                WorkDatabase workDatabase = workManagerImpl.getWorkDatabase();
                List<String> workSpecIds = workDatabase.getWorkIdByName(name);
                for (String workSpecId : workSpecIds) {
                    cancel(workManagerImpl, workSpecId);
                }
            }
        };
    }

    public static CancelWorkRunnable forAll(@NonNull final WorkManagerImpl workManagerImpl) {
        return new CancelWorkRunnable() {
            @WorkerThread
            @Override
            void runInternal() {
                Log.e("TUAN", "CancelWork all");
                WorkDatabase workDatabase = workManagerImpl.getWorkDatabase();
                List<String> workSpecIds = workDatabase.getAllUnfinishedWork();
                for (String workSpecId : workSpecIds) {
                    cancel(workManagerImpl, workSpecId);
                }
            }
        };
    }
}
