package com.changethejobid.evernotes;

import com.changethejobid.JobLauncher;
import com.evernote.android.job.JobRequest;

import java.util.concurrent.TimeUnit;

/**
 * @author itorba
 */

public class EvernoteJobLauncher implements JobLauncher {
    private static final long START_THRESHOLD = TimeUnit.SECONDS.toMillis(1);
    private static final long END_THRESHOLD = TimeUnit.SECONDS.toMillis(30);

    @Override
    public void launchJob() {
        new JobRequest.Builder(EvernoteJob.TAG)
                .setExecutionWindow(START_THRESHOLD, END_THRESHOLD)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setRequirementsEnforced(true)
                .build()
                .schedule();
    }
}
