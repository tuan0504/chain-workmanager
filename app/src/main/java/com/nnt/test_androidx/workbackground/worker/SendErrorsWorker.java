package com.nnt.test_androidx.workbackground.worker;

import android.content.Context;
import android.util.Log;

import com.nnt.test_worker.work.datatypes.Data;
import com.nnt.test_worker.work.OneTimeWorkRequest;
import com.nnt.test_worker.work.Worker;
import com.nnt.test_worker.work.datatypes.WorkerParameters;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class SendErrorsWorker extends Worker {

    private static final String EXTRA_NAME = "NAME";

    public SendErrorsWorker(Context context, WorkerParameters parameters) {
        super(context, parameters);
    }

    public static OneTimeWorkRequest scheduleInstantly(String name){
        Data data = new Data.Builder().putString(EXTRA_NAME, name).build();
        return new OneTimeWorkRequest.Builder(SendErrorsWorker.class)
                .setInputData(data)
                .addTag(SendErrorsWorker.class.getSimpleName())
                .build();
    }

    @Override
    public Result doWork() {
        final Result[] result = {Result.retry()};
        CountDownLatch countDownLatch = new CountDownLatch(1);

        final String inputData = getInputData().getString(EXTRA_NAME);
        final String out7 = getInputData().getString(EXTRA_NAME + "7");
        final String out6 = getInputData().getString(EXTRA_NAME + "6");
        final String[] out5 = getInputData().getStringArray(EXTRA_NAME + "!5");

//        if(out5 != null) {
//            Log.e("TUAN", "doWork 7:" + out7 + " 6:" + out6 + " !5:" + Arrays.asList(out5));
//        } else {
//            Log.e("TUAN", "doWork 7:" + out7 + " 6:" + out6 );
//        }

        new Thread(() -> {

            for (int i = 0 ; i <3 ;i++) {
                try {
                    Log.e("TUAN", "doWork :" + inputData + " -- " + i);
                    synchronized (this) {
                        wait(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

             if("worker 6".equals(inputData)) {
                try {
                    synchronized (this) { wait(5 * 1000); }
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Data data = new Data.Builder().putString(EXTRA_NAME + "6", inputData).build();
                result[0] = Result.success(data);
            } else if("worker 7".equals(inputData)) {
                 Data data = new Data.Builder().putString(EXTRA_NAME + "7", inputData).build();
                 result[0] = Result.failure(data);
             } else if(!"worker 5".equals(inputData)) {
                 String[] dataA = {inputData};
                 if(out5 != null) {
                     List<String> list = Arrays.asList(out5);
                     dataA = list.toArray(new String[list.size() + 1]);
                     dataA[list.size()] = inputData;
                 }
                 Data data = new Data.Builder().putStringArray(EXTRA_NAME + "!5", dataA).build();
                result[0] = Result.success(data);
            } else if ( getRunAttemptCount() < 2) {
                result[0] = Result.retry();
            } else {
                result[0] = Result.success();
            }

            countDownLatch.countDown();
        }).start();

        try {
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result[0];
    }

    @Override
    public void onStopped() {
        super.onStopped();
    }
}