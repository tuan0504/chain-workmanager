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

package com.nnt.test_worker.work.impl.runnable;

import android.util.Log;

import com.nnt.test_worker.work.WorkInfo;
import com.nnt.test_worker.work.impl.WorkDatabase;
import com.nnt.test_worker.work.impl.WorkManagerImpl;

public class StopWorkRunnable implements Runnable {

    private WorkManagerImpl mWorkManagerImpl;
    private String mWorkSpecId;

    public StopWorkRunnable(WorkManagerImpl workManagerImpl, String workSpecId) {
        mWorkManagerImpl = workManagerImpl;
        mWorkSpecId = workSpecId;
    }

    @Override
    public void run() {
        Log.e("TUAN", "StopWork " + mWorkSpecId);

        WorkDatabase workDatabase = mWorkManagerImpl.getWorkDatabase();
        if (workDatabase.getState(mWorkSpecId) == WorkInfo.State.RUNNING) {
            workDatabase.setState(WorkInfo.State.ENQUEUED, mWorkSpecId);
        }
        mWorkManagerImpl.getProcessor().stopWork(mWorkSpecId);
    }
}
