package com.changethejobid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.concurrent.TimeUnit;

/**
 * @author itorba
 */

class AlarmBasedJobLauncher extends BroadcastReceiver implements JobLauncher {
    private final Context appContext;
    private final Intent downloadServiceIntent;
    private final PendingIntent pendingIntent;
    static final String ACTION = AlarmBasedJobLauncher.class.getName();
    static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    static final String IS_DOWNLOADING_WAS_STARTED_EXTRA = ACTION + ".extra.IsDownloadingWasStarted";
    private static final long INITIAL_BACKOFF = TimeUnit.SECONDS.toMillis(30);
    private static final long MAX_BACKOFF = TimeUnit.MINUTES.toMillis(15);
    private int numberOfFailures;
    private final AlarmManager alarmManager;
    private final AlarmedBroadcastReceiver alarmedBroadcastReceiver;

    AlarmBasedJobLauncher(Context context) {
        appContext = context;
        downloadServiceIntent = new Intent(context, WorkingService.class);
        pendingIntent = PendingIntent.getBroadcast(appContext, 0, new Intent(AlarmedBroadcastReceiver.ACTION), 0);
        alarmManager = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);
        appContext.registerReceiver(this, new IntentFilter(AlarmBasedJobLauncher.ACTION));
        alarmedBroadcastReceiver = new AlarmedBroadcastReceiver();
    }

    @Override
    public void launchJob() {
        NetworkInfo networkInfo = getNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) { // need check network and run the service only if have connection
            Log.d(MainActivity.APP_TAG, "launchJob: Network Available. Start service");
            alarmManager.cancel(pendingIntent); // cancel previous request
            appContext.startService(downloadServiceIntent);
        } else {
            Log.d(MainActivity.APP_TAG, "launchJob: Doesn't have a network");
            registerConnectivityReceiver();
            fireBroadcastWithDelay(INITIAL_BACKOFF);
        }
    }

    @Nullable
    private NetworkInfo getNetworkInfo() {
        ConnectivityManager cm =
                (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm == null ? null : cm.getActiveNetworkInfo();
    }

    private void fireBroadcastWithDelay(long delay) {
        Log.d(MainActivity.APP_TAG, "fireBroadcastWithDelay() called with: delay = [" + delay + "]");
        alarmManager.cancel(pendingIntent);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delay, pendingIntent);
    }

    private void registerConnectivityReceiver() {
        Log.d(MainActivity.APP_TAG, "registerDownloadReceiver");
        IntentFilter intentFilter = new IntentFilter(CONNECTIVITY_CHANGE_ACTION); // need listen connectivity action at first
        intentFilter.addAction(AlarmedBroadcastReceiver.ACTION); // and internal action
        appContext.registerReceiver(new AlarmedBroadcastReceiver(), intentFilter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // need receive action from AlarmedBroadcastReceiver when we haven't network and reschedule the next network check
        Log.d(MainActivity.APP_TAG, "AlarmBasedJobLauncher onReceive() called with: action = [" + intent.getAction() + "]");
        boolean isDownloadingWasStarted = intent.getBooleanExtra(IS_DOWNLOADING_WAS_STARTED_EXTRA, false);
        if (isDownloadingWasStarted) {
            appContext.unregisterReceiver(this);
            appContext.unregisterReceiver(alarmedBroadcastReceiver);
        } else {
            numberOfFailures++;
            long delay = calculateDelay(numberOfFailures);
            resetFailuresIfNeed(delay);
            fireBroadcastWithDelay(delay);
        }
    }

    private long calculateDelay(int numberOfFailures) {
        return (long) (INITIAL_BACKOFF * Math.pow(2, numberOfFailures));
    }

    private void resetFailuresIfNeed(long delay) {
        if (delay >= MAX_BACKOFF) {
            numberOfFailures = 0;
        }
    }

}
