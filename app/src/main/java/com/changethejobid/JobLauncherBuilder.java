package com.changethejobid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
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
        Log.d(MainActivity.APP_TAG, "PackageName " + context.getPackageName());
    }

    JobLauncher build() {
        if (isApi21OrAbove()) {
            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            return new JobLauncherApi21Above(scheduler);
        } else if (isGoogleApiAvailable(context) && isClassLoaded("com.firebase.jobdispatcher.FirebaseJobDispatcher")) {
            FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
            return new JobLauncherApi20Below(dispatcher);
        }
        // TODO: 24.11.2017 Maybe need create static method in WorkingService to return intent
        PendingIntent pi = PendingIntent.getService(context, 0, new Intent(context, WorkingService.class), PendingIntent.FLAG_ONE_SHOT);
        return new AlarmBasedJobLauncher((AlarmManager) (context.getSystemService(Context.ALARM_SERVICE)), pi);
    }

    private boolean isApi21OrAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
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
            packageManager.getPackageInfo("com.google.android.gms", 64);
            Log.d(MainActivity.APP_TAG, "isGoogleApiAvailable true");
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(MainActivity.APP_TAG, "isGoogleApiAvailable false");
            return false;
        }
    }
}
