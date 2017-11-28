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

    void enqueueWork(Context context, int jobId) {
        enqueueWork(context, JobForAlarmManager.class, jobId, new Intent(""));
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d(MainActivity.APP_TAG, "onHandleWork() called with: intent = [" + intent + "]");
        if (hasConnection(getApplicationContext())) {
            // need stop scheduled alarm
            getApplicationContext().sendBroadcast(new Intent(AlarmReceiver.RESET_ALARM_ACTION));
            Log.d(MainActivity.APP_TAG, "launchJob: Network Available. Start service");
            // need start job service
            getApplicationContext().startService(new Intent(getApplicationContext(), WorkingService.class));
        } else {
            Log.d(MainActivity.APP_TAG, "launchJob: Doesn't have a network. Schedule alarm");
            scheduleAlarm(getApplicationContext());
        }
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

    private void scheduleAlarm(Context context) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(AlarmReceiver.SCHEDULE_ALARM_ACTION), PendingIntent.FLAG_ONE_SHOT);
        getAlarmManager(context).set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + TimeUnit.SECONDS.toMillis(30), pendingIntent);
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
