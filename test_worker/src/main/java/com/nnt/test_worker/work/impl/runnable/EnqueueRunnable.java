package com.nnt.test_worker.work.impl.runnable;

import static com.nnt.test_worker.work.datatypes.ExistingWorkPolicy.KEEP;
import static com.nnt.test_worker.work.datatypes.WorkInfo.State.BLOCKED;
import static com.nnt.test_worker.work.datatypes.WorkInfo.State.CANCELLED;
import static com.nnt.test_worker.work.datatypes.WorkInfo.State.ENQUEUED;
import static com.nnt.test_worker.work.datatypes.WorkInfo.State.FAILED;
import static com.nnt.test_worker.work.datatypes.WorkInfo.State.SUCCEEDED;

import android.text.TextUtils;

import com.nnt.test_worker.work.WorkRequest;
import com.nnt.test_worker.work.datatypes.ExistingWorkPolicy;
import com.nnt.test_worker.work.datatypes.WorkInfo;
import com.nnt.test_worker.work.datatypes.WorkSpec;
import com.nnt.test_worker.work.impl.WorkContinuationImpl;
import com.nnt.test_worker.work.impl.WorkDatabase;
import com.nnt.test_worker.work.impl.WorkManagerImpl;

import java.util.List;
import java.util.Set;

public class EnqueueRunnable implements Runnable {

    private final WorkContinuationImpl mWorkContinuation;

    public EnqueueRunnable(WorkContinuationImpl workContinuation) {
        mWorkContinuation = workContinuation;
    }

    @Override
    public void run() {
        try {
            if (mWorkContinuation.hasCycles()) {
                throw new IllegalStateException(String.format("WorkContinuation has cycles (%s)", mWorkContinuation));
            }
            boolean needsScheduling = addToDatabase();
            if (needsScheduling) {
                scheduleWorkInBackground();
            }
        } catch (Throwable exception) {
            exception.printStackTrace();
        }
    }

    public boolean addToDatabase() {
        return processContinuation(mWorkContinuation);
    }

    public void scheduleWorkInBackground() {
        WorkManagerImpl workManager = mWorkContinuation.getWorkManagerImpl();
        List<String> work = workManager.getWorkDatabase().getAllUnfinishedWork(mWorkContinuation.getAllIds());
        for (String workId : work) {
            if (workManager.getWorkDatabase().getState(workId) == ENQUEUED) {
                workManager.startWork(workId);
            }
        }
    }

    private static boolean processContinuation(WorkContinuationImpl workContinuation) {
        boolean needsScheduling = false;
        List<WorkContinuationImpl> parents = workContinuation.getParents();
        if (parents != null) {
            for (WorkContinuationImpl parent : parents) {
                if (!parent.isEnqueued()) {
                    needsScheduling |= processContinuation(parent);
                }
            }
        }
        needsScheduling |= enqueueContinuation(workContinuation);
        return needsScheduling;
    }

    private static boolean enqueueContinuation(WorkContinuationImpl workContinuation) {
        Set<String> prerequisiteIds = WorkContinuationImpl.prerequisitesFor(workContinuation);
        boolean needsScheduling = enqueueWorkWithPrerequisites(
                workContinuation.getWorkManagerImpl(),
                workContinuation.getWork(),
                prerequisiteIds.toArray(new String[0]),
                workContinuation.getName(),
                workContinuation.getExistingWorkPolicy());

        workContinuation.markEnqueued();
        return needsScheduling;
    }

    /**
     * Enqueues the {@link WorkSpec}'s while keeping track of the prerequisites.
     *
     * @return {@code true} If there is any scheduling to be done.
     */
    private static boolean enqueueWorkWithPrerequisites(
            WorkManagerImpl workManagerImpl,
            List<? extends WorkRequest> workList,
            String[] prerequisiteIds,
            String name,
            ExistingWorkPolicy existingWorkPolicy) {

        boolean needsScheduling = false;
        boolean hasPrerequisite = (prerequisiteIds != null && prerequisiteIds.length > 0);
        boolean hasCompletedAllPrerequisites = true;
        boolean hasFailedPrerequisites = false;
        boolean hasCancelledPrerequisites = false;

        WorkDatabase workDatabase = workManagerImpl.getWorkDatabase();

        if (hasPrerequisite) {
            for (String id : prerequisiteIds) {
                WorkSpec prerequisiteWorkSpec = workManagerImpl.getWorkDatabase().getWorkSpec(id);
                if (prerequisiteWorkSpec == null) {
                    return false;
                }

                WorkInfo.State prerequisiteState = prerequisiteWorkSpec.state;
                hasCompletedAllPrerequisites &= (prerequisiteState == SUCCEEDED);
                if (prerequisiteState == FAILED) {
                    hasFailedPrerequisites = true;
                } else if (prerequisiteState == CANCELLED) {
                    hasCancelledPrerequisites = true;
                }
            }
        }

        boolean isNamed = !TextUtils.isEmpty(name);
        boolean shouldApplyExistingWorkPolicy = isNamed && !hasPrerequisite;

        if (shouldApplyExistingWorkPolicy) {
            // Get everything with the unique tag.
            List<String> existingWorkSpecId = workDatabase.getWorkIdByName(name);

            if (!existingWorkSpecId.isEmpty()) {
                if (existingWorkPolicy == KEEP) {
                    for (String id : existingWorkSpecId) {
                        if (!workDatabase.getState(id).isFinished()) {
                            return false;
                        }
                    }
                } else {
                    CancelWorkRunnable.forName(name, workManagerImpl).run();
                    needsScheduling = true;

                    for (String id : existingWorkSpecId) {
                        workDatabase.deleteWorkSpec(id);
                    }
                }
            }
        }

        for (WorkRequest work : workList) {
            WorkSpec workSpec = work.getWorkSpec();

            if (hasPrerequisite && !hasCompletedAllPrerequisites) {
                if (hasFailedPrerequisites) {
                    workSpec.state = FAILED;
                } else if (hasCancelledPrerequisites) {
                    workSpec.state = CANCELLED;
                } else {
                    workSpec.state = BLOCKED;
                }
            }

            if (workSpec.state == ENQUEUED) {
                needsScheduling = true;
            }

            saveDatabase(workDatabase, workSpec, hasPrerequisite, prerequisiteIds, work, name);
        }
        return needsScheduling;
    }

    private static void saveDatabase(WorkDatabase workDatabase, WorkSpec workSpec,
                                     Boolean hasPrerequisite, String[] prerequisiteIds, WorkRequest work, String name) {
        workDatabase.insertWorkSpec(workSpec);
        if (hasPrerequisite) {
            for (String prerequisiteId : prerequisiteIds) {
                workDatabase.insertDependentWorkId(prerequisiteId, work.getStringId());
            }
        }

        for (String tag : work.getTags()) {
            workDatabase.insertWorkTag(tag, work.getStringId());
        }

        if (!TextUtils.isEmpty(name)) {
            workDatabase.insertWorkName(name, work.getStringId());
        }
    }
}
