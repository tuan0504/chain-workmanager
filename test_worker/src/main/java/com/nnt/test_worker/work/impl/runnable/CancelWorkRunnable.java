package com.nnt.test_worker.work.impl.runnable;

import static com.nnt.test_worker.work.datatypes.WorkInfo.State.CANCELLED;

import com.nnt.test_worker.work.datatypes.WorkInfo;
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

    public static CancelWorkRunnable forId(final UUID id, final WorkManagerImpl workManagerImpl) {
        return new CancelWorkRunnable() {

            @Override
            void runInternal() {
                cancel(workManagerImpl, id.toString());
            }
        };
    }

    public static CancelWorkRunnable forTag(final String tag, final WorkManagerImpl workManagerImpl) {
        return new CancelWorkRunnable() {
            @Override
            void runInternal() {
                WorkDatabase workDatabase = workManagerImpl.getWorkDatabase();
                List<String> workSpecIds = workDatabase.getWorkIdByTag(tag);
                for (String workSpecId : workSpecIds) {
                    cancel(workManagerImpl, workSpecId);
                }
            }
        };
    }

    public static CancelWorkRunnable forName(
            final String name,
            final WorkManagerImpl workManagerImpl) {
        return new CancelWorkRunnable() {

            @Override
            void runInternal() {
                WorkDatabase workDatabase = workManagerImpl.getWorkDatabase();
                List<String> workSpecIds = workDatabase.getWorkIdByName(name);
                for (String workSpecId : workSpecIds) {
                    cancel(workManagerImpl, workSpecId);
                }
            }
        };
    }

    public static CancelWorkRunnable forAll(final WorkManagerImpl workManagerImpl) {
        return new CancelWorkRunnable() {

            @Override
            void runInternal() {
                WorkDatabase workDatabase = workManagerImpl.getWorkDatabase();
                List<String> workSpecIds = workDatabase.getAllUnfinishedWork();
                for (String workSpecId : workSpecIds) {
                    cancel(workManagerImpl, workSpecId);
                }
            }
        };
    }
}
