package com.changethejobid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import java.util.concurrent.TimeUnit;

/**
 * @author itorba
 */

public class JobForAlarmManager extends JobIntentService {
    static final String ATTEMPTS_EXTRA = JobForAlarmManager.class.getName() + ".extra.ATTEMPTS";
    static final String JOB_ID_EXTRA = JobForAlarmManager.class.getName() + ".extra.JOB_ID";
    private static final String STARTUP_ACTION = JobForAlarmManager.class.getName() + ".action.STARTUP";
    static final int DEFAULT_VALUE = -128;
    private static final long INITIAL_BACKOFF = TimeUnit.SECONDS.toMillis(30);
    private static final long MAX_BACKOFF = TimeUnit.MINUTES.toMillis(15);
    private JobStateHolder jobStateHolder;

    void enqueueWork(Context context, int jobId) {
        enqueueWorkWithData(context, jobId, DEFAULT_VALUE);
    }

    void enqueueWorkWithData(Context context, int jobId, int attempts) {
        enqueueWork(context, JobForAlarmManager.class, jobId, getDefaultIntent(jobId, attempts));
    }

    private Intent getDefaultIntent(int jobId, int attempts) {
        return new Intent(STARTUP_ACTION)
                .putExtra(JOB_ID_EXTRA, jobId)
                .putExtra(ATTEMPTS_EXTRA, attempts);
    }


    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        jobStateHolder = ((JobsApplication) getApplication()).getJobStateHolder();
        int jobId = extractJobId(intent);
        Log.d(MainActivity.APP_TAG, "JobForAlarmManager onHandleWork() called");
        if (STARTUP_ACTION.equals(intent.getAction())) {
            // add job id to holder
            jobStateHolder.addJob(jobId);
            if (hasConnection(getApplicationContext()) && !isJobWorkingNow(jobId)) {
                // need stop scheduled alarm
                cancelAlarm(getApplicationContext());
                Log.d(MainActivity.APP_TAG, "JobForAlarmManager launchJob: Network Available. Start service");
                // need start job service
                getApplicationContext().startService(new Intent(getApplicationContext(), WorkingService.class).putExtra(JOB_ID_EXTRA, jobId));
                jobStateHolder.updateJobState(jobId, JobState.WORKING);
            } else {
                Log.d(MainActivity.APP_TAG, "JobForAlarmManager launchJob: Doesn't have a network. Schedule alarm");
                scheduleAlarm(getApplicationContext(), intent);
            }
        }
    }

    private int extractJobId(Intent intent) {
        int jobId = intent.getIntExtra(JOB_ID_EXTRA, DEFAULT_VALUE);
        if (jobId == DEFAULT_VALUE) {
            throw new IllegalStateException("JobId unspecified");
        }
        return jobId;
    }

    private boolean isJobWorkingNow(int jobId) {
        JobState state = jobStateHolder.getJobState(jobId);
        return JobState.WORKING.equals(state);
    }

    private void scheduleAlarm(Context context, Intent intent) {
        int numberOfFailures = extractNumberOfFailures(intent);
        long delay = calculateDelay(numberOfFailures);
        numberOfFailures = increaseFailuresIfNeed(delay, numberOfFailures);
        Log.d(MainActivity.APP_TAG, "JobForAlarmManager. new number of failures = [" + numberOfFailures + "]");
        Intent alarmIntent = new Intent(AlarmReceiver.SCHEDULE_ALARM_ACTION);
        alarmIntent.putExtra(ATTEMPTS_EXTRA, numberOfFailures);
        PendingIntent pendingIntent = createPendingIntent(context, alarmIntent, true);
        Log.d(MainActivity.APP_TAG, "JobForAlarmManager. Reschedule with delay = [" + delay + "], new number of failures = [" + numberOfFailures + "]");
        getAlarmManager(context).set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delay, pendingIntent);
    }

    private void cancelAlarm(Context context) {
        Intent alarmIntent = new Intent(AlarmReceiver.SCHEDULE_ALARM_ACTION);
        PendingIntent pendingIntent = createPendingIntent(context, alarmIntent, false);
        getAlarmManager(context).cancel(pendingIntent);
    }

    private int extractNumberOfFailures(Intent intent) {
        int numberOfFailures = intent.getIntExtra(ATTEMPTS_EXTRA, DEFAULT_VALUE);
        return numberOfFailures == DEFAULT_VALUE ? 1 : numberOfFailures;
    }

    private PendingIntent createPendingIntent(Context context, Intent intent, boolean repeating) {
        return PendingIntent.getBroadcast(context, 0, intent, createPendingIntentFlags(repeating));
    }

    protected int createPendingIntentFlags(boolean repeating) {
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (!repeating) {
            flags |= PendingIntent.FLAG_ONE_SHOT;
        }
        return flags;
    }

    private long calculateDelay(int numberOfFailures) {
        return (long) (INITIAL_BACKOFF * Math.pow(2, numberOfFailures - 1.0));
    }

    private int increaseFailuresIfNeed(long delay, int numberOfFailures) {
        if (delay < MAX_BACKOFF) {
            return ++numberOfFailures;
        } else return numberOfFailures;
    }

    @Nullable
    private NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm == null ? null : cm.getActiveNetworkInfo();
    }

    private boolean hasConnection(Context context) {
        NetworkInfo networkInfo = getNetworkInfo(context);
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private AlarmManager getAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public void onDestroy() {
        Log.d(MainActivity.APP_TAG, "JobForAlarmManager onDestroy() called");
        super.onDestroy();
    }


}
