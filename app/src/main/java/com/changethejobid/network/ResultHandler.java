package com.changethejobid.network;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author itorba
 */

class ResultHandler {
    private final ExecutorService resultProcessor = Executors.newSingleThreadExecutor();
    private final DownloadableExecutionCallback executionCallback;

    ResultHandler(DownloadableExecutionCallback callback) {
        executionCallback = callback;
    }

    void processResults(final List<Future<DownloadableResult>> results) {
        resultProcessor.execute(new Runnable() {
            @Override
            public void run() {
                for (Future<DownloadableResult> future : results) {
                    try {
                        DownloadableResult result = future.get();
                        if (result.isOk()) {
                            executionCallback.onSuccess(result.getId());
                        } else {
                            executionCallback.onError(result.getId());
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                }
                executionCallback.onComplete();
            }
        });
    }


}
