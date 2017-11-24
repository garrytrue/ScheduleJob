package com.changethejobid.network;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author itorba
 */

public class DownloadManager implements DownloadableExecutionCallback {
    private OnDownloadsFinishedListener onDownloadsFinishedListener;
    private final List<Future<DownloadableResult>> results;
    private final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(4);
    private final DownloadablesRepo downloadablesRepo;

    public DownloadManager(DownloadablesRepo downloadablesRepo) {
        results = new ArrayList<>();
        this.downloadablesRepo = downloadablesRepo;
    }

    public void setOnDownloadsFinishedListener(OnDownloadsFinishedListener listener) {
        onDownloadsFinishedListener = listener;
    }


    public void startLoadContent() {
        for (Downloadable downloadable : downloadablesRepo.getDownloadables()) {
            Future<DownloadableResult> resultFuture = fixedThreadPool.submit(downloadable);
            results.add(resultFuture);
        }
        new ResultHandler(this).processResults(results);
    }

    @Override
    public void onSuccess(String id) {
        downloadablesRepo.removeSucceed(id);
    }

    @Override
    public void onError(String id) {
        // don't DDOS backend or maybe you don't have connection
    }

    @Override
    public void onComplete() {
        if (downloadablesRepo.isAllCompletedWithSuccess()) {
            onDownloadsFinishedListener.onAllDownloadFinished();
        } else {
            onDownloadsFinishedListener.onFinishedWithError();
        }
    }
}
