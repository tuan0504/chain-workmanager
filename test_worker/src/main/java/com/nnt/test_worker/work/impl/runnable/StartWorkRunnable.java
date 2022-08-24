package com.nnt.test_worker.work.impl.runnable;

import com.nnt.test_worker.work.impl.WorkManagerImpl;

public class StartWorkRunnable implements Runnable {

    private WorkManagerImpl mWorkManagerImpl;
    private String mWorkSpecId;

    public StartWorkRunnable(WorkManagerImpl workManagerImpl, String workSpecId) {
        mWorkManagerImpl = workManagerImpl;
        mWorkSpecId = workSpecId;
    }

    @Override
    public void run() {
        mWorkManagerImpl.getProcessor().startWork(mWorkSpecId);
    }
}
