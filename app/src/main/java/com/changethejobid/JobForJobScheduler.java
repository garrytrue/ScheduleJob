package com.changethejobid;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

/**
 * @author itorba
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobForJobScheduler extends JobService {
    private static final String TAG = "JobForJobScheduler";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(MainActivity.APP_TAG + " " + TAG, "onStartJob() called with: jobParameters = [" + jobParameters + "]");
        getApplicationContext().startService(new Intent(getApplicationContext(), WorkingService.class));
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(MainActivity.APP_TAG + " " + TAG, "onStopJob() called with: jobParameters = [" + jobParameters + "]");
        return false;
    }
}
