package com.nnt.test_worker.work.impl;

import android.util.Pair;

import com.nnt.test_worker.work.datatypes.Data;
import com.nnt.test_worker.work.datatypes.WorkInfo;
import com.nnt.test_worker.work.datatypes.WorkSpec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class WorkDatabase {
    private final WorkDatabaseObservable workerObservable = new WorkDatabaseObservable();

    private final ConcurrentHashMap<String, WorkSpec> workSpecsDao = new ConcurrentHashMap<>(); //first : workSpecId , second: WorkSpec
    private final List<Pair<String, String>> dependentWorkIdsDaos = new ArrayList<>(); //first : workSpecId , second: dependent workID
    private final List<Pair<String, String>> workTagsDao = new ArrayList<>(); //first : Tag Name , second: ID
    private final ConcurrentHashMap<String, List<String>> workNameDao = new ConcurrentHashMap<>(); //first : name , second: workSpecId

    //begin: WorkSpec Dao
    public WorkSpec getWorkSpec(String workSpecId) {
        return workSpecsDao.get(workSpecId);
    }

    public void deleteWorkSpec(String workSpecId) {
        workSpecsDao.remove(workSpecId);
    }

    public void insertWorkSpec(WorkSpec workSpec) {
        workSpecsDao.put(workSpec.id, workSpec);
    }

    public WorkDatabase() {
    }

    public void setState(WorkInfo.State state, String workSpecId) {
        WorkSpec workSpec = workSpecsDao.get(workSpecId);
        if (workSpec != null && workSpec.state != state) {
            workSpec.state = state;
            workerObservable.stateChangeInWorkSpec(workSpec);
        }
    }

    public WorkInfo.State getState(String workSpecId) {
        WorkSpec workSpec = workSpecsDao.get(workSpecId);
        return workSpec != null ? workSpec.state : null;
    }

    public void setWorkSpecOutput(String workSpecId, Data output) {
        WorkSpec workSpec = workSpecsDao.get(workSpecId);
        if (workSpec != null) {
            workSpec.output = output;
        }
    }

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
    public List<String> getDependentWorkIds(String workSpecId) {
        List<String> result = new ArrayList();
        List<Pair<String, String>> workIds = new CopyOnWriteArrayList(dependentWorkIdsDaos);
        for (Pair<String, String> item : workIds) {
            if (item.first.equals(workSpecId)) {
                result.add(item.second);
            }
        }
        return result;
    }

    public List<String> getPrerequisiteWorkIds(String workSpecId) {
        List<String> result = new ArrayList();
        List<Pair<String, String>> workIds = new CopyOnWriteArrayList(dependentWorkIdsDaos);
        for (Pair<String, String> item : workIds) {
            if (item.second.equals(workSpecId)) {
                result.add(item.first);
            }
        }
        return result;
    }

    public void insertDependentWorkId(String workSpecId, String dependentId) {
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

    public List<Data> getInputsFromPrerequisites(String workSpecId) {
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
    public List<String> getWorkIdByTag(String tag) {
        List<String> result = new ArrayList();
        List<Pair<String, String>> workTags = new CopyOnWriteArrayList(workTagsDao);
        for (Pair<String, String> item : workTags) {
            if (item.first.equals(tag)) {
                result.add(item.second);
            }
        }
        return result;
    }


    public List<String> getTagsByWorkId(String workSpecId) {
        List<String> result = new ArrayList();
        List<Pair<String, String>> workTags = new CopyOnWriteArrayList(workTagsDao);
        for (Pair<String, String> item : workTags) {
            if (item.second.equals(workSpecId)) {
                result.add(item.first);
            }
        }
        return result;
    }

    public void insertWorkTag(String workTag, String workSpecId) {
        workTagsDao.add(Pair.create(workTag, workSpecId));
    }
    //end: workTag Dao

    //begin : WorkName Dao
    public List<String> getWorkIdByName(String uniqueWorkName) {
        List<String> result = workNameDao.get(uniqueWorkName);
        if (result == null) {
            return new ArrayList();
        }
        return result;
    }

    public void insertWorkName(String name, String workSpecId) {
        workNameDao.put(name, Collections.singletonList(workSpecId));
    }
    //end: WorkName Dao

    //begin : WorkInfo Observable
    public ObservableItem<WorkInfo> getWorkInfoById(String workSpecId) {
        final ObservableItem<WorkInfo> observableItem = new ObservableItem<>();
        workerObservable.addObserver((observable, workSpec) -> {
            if (workSpec instanceof WorkSpec && workSpecId.equals(((WorkSpec) workSpec).id)) {
                WorkSpec data = (WorkSpec) workSpec;
                WorkInfo info = new WorkInfo(UUID.fromString(data.id), data.state, data.output);
                observableItem.updateData(info);
            }
        });

        return observableItem;
    }

    public ObservableItem<List<WorkInfo>> getWorkInfoByListIds(List<String> workSpecIds) {
        final ObservableItem<List<WorkInfo>> observableItem = new ObservableItem<>();
        workerObservable.addObserver((observable, workSpec) -> {
            if (workSpec instanceof WorkSpec && workSpecIds.contains(((WorkSpec) workSpec).id)) {
                boolean isUpdate = false;
                String workId = ((WorkSpec) workSpec).id;
                WorkInfo.State state = ((WorkSpec) workSpec).state;
                List<WorkInfo> cached = observableItem.getData();

                //Check should we notify update Observable
                if(cached == null || cached.isEmpty()) {
                    isUpdate = true;
                } else {
                    for(WorkInfo info : cached) {
                        if(workId.equals(info.getId().toString()) && state != info.getState()) {
                            isUpdate = true;
                            break;
                        }
                    }
                }

                //Notify update observable
                if(isUpdate) {
                    List<WorkInfo> result = new ArrayList<>();
                    for (String id : workSpecIds) {
                        WorkSpec item = workSpecsDao.get(id);
                        if (item != null) {
                            result.add(new WorkInfo(UUID.fromString(id), item.state, item.output));
                        }
                    }
                    observableItem.updateData(result);
                }
            }
        });

        return observableItem;
    }
    //end : WorkInfo Observable

    //Observable items
    public static class ObservableItem<T> extends Observable {
        private T cache = null;

        public T getData() {
            return cache;
        }

        public void updateData(T data) {
            cache = data;
            if (countObservers() > 0) {
                setChanged();
                notifyObservers(data);
            }
        }

        public void addHotObserver(Observer observer) {
            addObserver(observer);
            if (cache != null) {
                observer.update(this, cache);
            }
        }
    }

    //Observable work database
    private static class WorkDatabaseObservable extends Observable {
        public void stateChangeInWorkSpec(WorkSpec workSpec) {
            setChanged();
            notifyObservers(workSpec);
        }
    }
}
