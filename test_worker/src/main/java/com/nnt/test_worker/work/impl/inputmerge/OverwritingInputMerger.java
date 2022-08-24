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

package com.nnt.test_worker.work.impl.inputmerge;

import android.support.annotation.NonNull;

import com.nnt.test_worker.work.Data;
import com.nnt.test_worker.work.InputMerger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class OverwritingInputMerger extends InputMerger {

    @Override
    public @NonNull
    Data merge(@NonNull List<Data> inputs) {
        Data.Builder output = new Data.Builder();
        Map<String, Object> mergedValues = new HashMap<>();

        for (Data input : inputs) {
            mergedValues.putAll(input.getKeyValueMap());
        }

        output.putAll(mergedValues);
        return output.build();
    }
}
