package com.nnt.test_worker.work.inputmerge;

import com.nnt.test_worker.work.datatypes.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class OverwritingInputMerger extends InputMerger {

    @Override
    public Data merge(List<Data> inputs) {
        Data.Builder output = new Data.Builder();
        Map<String, Object> mergedValues = new HashMap<>();

        for (Data input : inputs) {
            mergedValues.putAll(input.getKeyValueMap());
        }

        output.putAll(mergedValues);
        return output.build();
    }
}
