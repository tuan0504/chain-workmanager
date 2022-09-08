package com.nnt.test_worker.work.inputmerge;

import com.nnt.test_worker.work.datatypes.Data;

import java.util.List;

public abstract class InputMerger {
    public abstract Data merge(List<Data> inputs);
}
