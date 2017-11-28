package com.changethejobid;

import android.content.Intent;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * @author itorba
 */

public class JobForJobDispatcher extends JobService {
    private static final String TAG = "JobForJobDispatcher";

    @Override
    public boolean onStartJob(JobParameters job) {
        Log.d(MainActivity.APP_TAG + " " + TAG, "onStartJob() called with: jobParameters = [" + job + "]");
        getApplicationContext().startService(new Intent(getApplicationContext(), WorkingService.class));
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Log.d(MainActivity.APP_TAG + " " + TAG, "onStopJob() called with: jobParameters = [" + job + "]");
        return false;
    }
}
