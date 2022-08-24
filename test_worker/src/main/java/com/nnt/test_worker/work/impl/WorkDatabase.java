package com.nnt.test_worker.work.impl;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.nnt.test_worker.work.Data;
import com.nnt.test_worker.work.WorkInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class WorkDatabase {

    private final MutableLiveData<Pair<String, WorkInfo.State>> workStateUpdate = new MutableLiveData<>();
    private final MediatorLiveData<HashMap<String, WorkInfo.State>> workInfosDao = new MediatorLiveData<>();

    private final ConcurrentHashMap<String, WorkSpec> workSpecsDao = new ConcurrentHashMap<>();       //first : workSpecId , second: WorkSpec
    private final List<Pair<String, String>> dependentWorkIdsDaos = new ArrayList<>();  //first : workSpecId , second: dependent workID
    private final List<Pair<String, String>> workTagsDao = new ArrayList<>(); //first : Tag Name , second: ID
    private final ConcurrentHashMap<String, List<String>> workNameDao = new ConcurrentHashMap<>();       //first : name , second: workSpecId

    //begin: WorkSpec Dao
    public WorkSpec getWorkSpec(@NonNull String workSpecId) {
        return workSpecsDao.get(workSpecId);
    }

    public void deleteWorkSpec(@NonNull String workSpecId) {
        workSpecsDao.remove(workSpecId);
    }

    public void insertWorkSpec(@NonNull WorkSpec workSpec) {
        workSpecsDao.put(workSpec.id, workSpec);
    }

    public WorkDatabase() {
        workInfosDao.addSource(workStateUpdate, stringStatePair -> {
            String workId = stringStatePair.first;
            WorkInfo.State state = stringStatePair.second;
            HashMap<String, WorkInfo.State> data = workInfosDao.getValue();

            if (data != null) {
                if (state != data.get(workId)) {
                    data.put(workId, state);
                    workInfosDao.postValue(data);
                }
            } else {
                data = new HashMap<>();
                data.put(workId, state);
                workInfosDao.postValue(data);
            }
        });
    }

    public void setState(@NonNull WorkInfo.State state, @NonNull String workSpecId) {
        WorkSpec workSpec = workSpecsDao.get(workSpecId);
        if (workSpec != null && workSpec.state != state) {
            workStateUpdate.postValue(Pair.create(workSpecId, state));
            workSpec.state = state;
        }
    }

    public WorkInfo.State getState(@NonNull String workSpecId) {
        WorkSpec workSpec = workSpecsDao.get(workSpecId);
        return workSpec != null ? workSpec.state : null;
    }

    public void incrementWorkSpecRunAttemptCount(@NonNull String workSpecId) {
        WorkSpec workSpec = workSpecsDao.get(workSpecId);
        if (workSpec != null) {
            workSpec.runAttemptCount++;
        }
    }

    public void setWorkSpecOutput(@NonNull String workSpecId, @NonNull Data output) {
        WorkSpec workSpec = workSpecsDao.get(workSpecId);
        if (workSpec != null) {
            workSpec.output = output;
        }
    }

    @NonNull
    public List<String> getAllUnfinishedWork() {
        List<String> result = new ArrayList();
        List<WorkSpec> workSpecs = new CopyOnWriteArrayList<>(workSpecsDao.values());
        for (WorkSpec workSpec : workSpecs) {
            if (!workSpec.state.isFinished()) {
                result.add(workSpec.id);
            }
        }
        return result;
    }

    @NonNull
    public List<String> getAllUnfinishedWork(List<String> allIds) {
        List<String> result = new ArrayList();
        for (String id : allIds) {
            WorkSpec workSpec = workSpecsDao.get(id);
            if (workSpec != null && !workSpec.state.isFinished()) {
                result.add(workSpec.id);
            }
        }
        return result;
    }
    //end: WorkSpec Dao


    //begin: Dependency Dao
    @NonNull
    public List<String> getDependentWorkIds(@NonNull String workSpecId) {
        List<String> result = new ArrayList();
        List<Pair<String, String>> workIds = new CopyOnWriteArrayList(dependentWorkIdsDaos);
        for (Pair<String, String> item : workIds) {
            if (item.first.equals(workSpecId)) {
                result.add(item.second);
            }
        }
        return result;
    }

    @NonNull
    public List<String> getPrerequisiteWorkIds(@NonNull String workSpecId) {
        List<String> result = new ArrayList();
        List<Pair<String, String>> workIds = new CopyOnWriteArrayList(dependentWorkIdsDaos);
        for (Pair<String, String> item : workIds) {
            if (item.second.equals(workSpecId)) {
                result.add(item.first);
            }
        }
        return result;
    }

    public void insertDependentWorkId(@NonNull String workSpecId, @NonNull String dependentId) {
        dependentWorkIdsDaos.add(Pair.create(workSpecId, dependentId));
    }

    public boolean hasCompletedAllPrerequisites(String workSpecId) {
        List<String> prerequisiteIds = getPrerequisiteWorkIds(workSpecId);
        for (String id : prerequisiteIds) {
            WorkSpec workSpec = workSpecsDao.get(id);
            if (workSpec != null && !workSpec.state.isFinished()) {
                return false;
            }
        }
        return true;
    }

    @NonNull
    public List<Data> getInputsFromPrerequisites(@NonNull String workSpecId) {
        List<String> workIds = getPrerequisiteWorkIds(workSpecId);
        List<Data> result = new ArrayList();
        for (String id : workIds) {
            WorkSpec workSpec = workSpecsDao.get(id);
            if (workSpec != null) {
                result.add(workSpec.output);
            }
        }
        return result;
    }
    //end: Dependency Dao

    //begin: workTag Dao
    @NonNull
    public List<String> getWorkIdByTag(@NonNull String tag) {
        List<String> result = new ArrayList();
        List<Pair<String, String>> workTags = new CopyOnWriteArrayList(workTagsDao);
        for (Pair<String, String> item : workTags) {
            if (item.first.equals(tag)) {
                result.add(item.second);
            }
        }
        return result;
    }

    @NonNull
    public List<String> getTagsByWorkId(@NonNull String workSpecId) {
        List<String> result = new ArrayList();
        List<Pair<String, String>> workTags = new CopyOnWriteArrayList(workTagsDao);
        for (Pair<String, String> item : workTags) {
            if (item.second.equals(workSpecId)) {
                result.add(item.first);
            }
        }
        return result;
    }

    public void insertWorkTag(@NonNull String workTag, @NonNull String workSpecId) {
        workTagsDao.add(Pair.create(workTag, workSpecId));
    }
    //end: workTag Dao

    //begin : WorkName Dao
    @NonNull
    public List<String> getWorkIdByName(@NonNull String uniqueWorkName) {
        List<String> result = workNameDao.get(uniqueWorkName);
        if (result == null) {
            return new ArrayList();
        }
        return result;
    }

    public void insertWorkName(@NonNull String name, @NonNull String workSpecId) {
        workNameDao.put(name, Collections.singletonList(workSpecId));
    }
    //end: WorkName Dao

    //begin : WorkInfo LiveData
    @NonNull
    public LiveData<WorkInfo> getWorkInfo(@NonNull String workSpecId) {
        MediatorLiveData<WorkInfo> liveData = new MediatorLiveData<>();
        liveData.addSource(workInfosDao, workInfoDao -> {
            WorkInfo.State state = getState(workSpecId);
            WorkInfo info = liveData.getValue();

            if (info == null || state != info.getState()) {
                WorkSpec workSpec = workSpecsDao.get(workSpecId);

                if (workSpec != null && state != null) {
                    liveData.setValue(new WorkInfo(UUID.fromString(workSpecId), state, workSpec.output, new ArrayList<>()));
                }
            }
        });

        return liveData;
    }

    @NonNull
    public LiveData<List<WorkInfo>> getWorkInfoByTags(@NonNull String tag) {
        List<String> workIdsWithTag = getWorkIdByTag(tag);
        if (workIdsWithTag.isEmpty()) {
            return new MutableLiveData<>();
        }
        return getWorkInfoByListIds(workIdsWithTag);
    }

    @NonNull
    public LiveData<List<WorkInfo>> getWorkInfoForUniqueWorkName(@NonNull String name) {
        List<String> result = workNameDao.get(name);
        if (result == null) {
            return new MutableLiveData<>();
        }
        return getWorkInfoByListIds(result);
    }

    @NonNull
    public LiveData<List<WorkInfo>> getWorkInfoByListIds(@NonNull List<String> workSpecIds) {
        MediatorLiveData<List<WorkInfo>> liveData = new MediatorLiveData<>();
        liveData.addSource(workInfosDao, workInfoDao -> {
            List<WorkInfo> liveDataValues = liveData.getValue();
            boolean isUpdate = false;
            if (liveDataValues == null) {
                isUpdate = true;
            } else {
                for (WorkInfo info : liveDataValues) {
                    WorkInfo.State state = getState(info.getId().toString());

                    if (state != info.getState()) {
                        isUpdate = true;
                        break;
                    }
                }
            }

            if (isUpdate) {
                List<WorkInfo> result = new ArrayList<>();
                for (String id : workSpecIds) {
                    WorkSpec workSpec = workSpecsDao.get(id);

                    if (workSpec != null) {
                        result.add(new WorkInfo(UUID.fromString(id), workSpec.state, workSpec.output, new ArrayList<>()));
                    }
                }

                liveData.setValue(result);
            }
        });

        return liveData;
    }
    //end : WorkInfo LiveData
}
