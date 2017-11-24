package com.changethejobid;

import android.app.Application;

import com.changethejobid.network.DownloadManager;
import com.changethejobid.network.DownloadablesRepo;

/**
 * @author itorba
 */

public class JobsApplication extends Application {
    private DownloadManager downloadManager;
    private DownloadablesRepo downloadablesRepo;

    @Override
    public void onCreate() {
        super.onCreate();
        downloadablesRepo = new DownloadablesRepo();
        downloadManager = new DownloadManager(downloadablesRepo);
    }

    public DownloadManager getDownloadManager() {
        return downloadManager;
    }

    public DownloadablesRepo getDownloadablesRepo() {
        return downloadablesRepo;
    }
}
