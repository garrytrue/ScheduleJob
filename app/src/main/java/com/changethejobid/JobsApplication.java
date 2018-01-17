package com.changethejobid;

import android.app.Application;

import com.changethejobid.evernotes.EvernoteJobCreator;
import com.changethejobid.network.DownloadManager;
import com.changethejobid.network.DownloadablesRepo;
import com.evernote.android.job.JobManager;

/**
 * @author itorba
 */

public class JobsApplication extends Application {
    private DownloadManager downloadManager;
    private DownloadablesRepo downloadablesRepo;
    private JobStateHolder jobStateHolder;

    @Override
    public void onCreate() {
        super.onCreate();
        downloadablesRepo = new DownloadablesRepo();
        downloadManager = new DownloadManager(downloadablesRepo);
        jobStateHolder = new JobStateHolder();
        JobManager.create(this).addJobCreator(new EvernoteJobCreator());
    }

    public DownloadManager getDownloadManager() {
        return downloadManager;
    }

    public DownloadablesRepo getDownloadablesRepo() {
        return downloadablesRepo;
    }

    public JobStateHolder getJobStateHolder() {
        return jobStateHolder;
    }
}
