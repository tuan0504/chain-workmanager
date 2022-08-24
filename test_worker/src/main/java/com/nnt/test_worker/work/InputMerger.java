package com.nnt.test_worker.work;

import android.support.annotation.NonNull;

import java.util.List;

public abstract class InputMerger {
    public abstract @NonNull Data merge(@NonNull List<Data> inputs);
}
