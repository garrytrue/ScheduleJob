package com.changethejobid;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.support.annotation.RequiresApi;

/**
 * @author itorba
 */
@RequiresApi(21)
        //Lollipop and above
class JobLauncherForJobScheduler implements JobLauncher {
    private static final int DOWNLOAD_SERVICE_ID = 27041983;
    private final JobScheduler scheduler;
    private final JobInfo jobInfo;

    JobLauncherForJobScheduler(JobScheduler scheduler) {
        this.scheduler = scheduler;
        jobInfo = createJobInfo();
    }

    @Override
    public void launchJob() {
        scheduler.schedule(jobInfo);
    }

    private JobInfo createJobInfo() {
        ComponentName componentName = new ComponentName("com.changethejobid", JobForJobScheduler.class.getName());
        return new JobInfo.Builder(DOWNLOAD_SERVICE_ID, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();
    }
}
