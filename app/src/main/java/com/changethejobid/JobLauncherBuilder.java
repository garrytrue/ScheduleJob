package com.changethejobid;

import android.app.job.JobScheduler;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;

/**
 * @author itorba
 */

class JobLauncherBuilder {
    private final Context context;

    JobLauncherBuilder(Context context) {
        this.context = context;
    }

    JobLauncher build() {
        if (isApi21OrAbove()) {
            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            return new JobLauncherForJobScheduler(scheduler);
        } else if (isGoogleApiAvailable(context) && isClassLoaded("com.firebase.jobdispatcher.FirebaseJobDispatcher")) {
            FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
            return new JobLauncherForJobDispatcher(dispatcher);
        }
        return new AlarmBasedJobLauncher(context);
    }

    private boolean isClassLoaded(String className) {
        Log.d(MainActivity.APP_TAG, "isClassLoaded() called with: className = [" + className + "]");
        try {
            Class.forName(className);
            Log.d(MainActivity.APP_TAG, "isClassLoaded() result true");
            return true;
        } catch (ClassNotFoundException e) {
            Log.d(MainActivity.APP_TAG, "isClassLoaded() result false");
            return false;
        }
    }

    private boolean isGoogleApiAvailable(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            // COPY/PASTE from GoogleApiAvailability internal classes
            packageManager.getPackageInfo("com.google.android.gms", PackageManager.GET_RESOLVED_FILTER);
            Log.d(MainActivity.APP_TAG, "isGoogleApiAvailable true");
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(MainActivity.APP_TAG, "isGoogleApiAvailable false");
            return false;
        }
    }

    private boolean isApi21OrAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
