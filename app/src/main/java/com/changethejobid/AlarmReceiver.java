package com.changethejobid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.concurrent.TimeUnit;

/**
 * This receiver is self restart receiver and listens two actions. First is {@link AlarmReceiver#SCHEDULE_ALARM_ACTION} for schedule alarm
 * Next one is {@link AlarmReceiver#RESET_ALARM_ACTION}
 *
 * @author itorba
 */

public class AlarmReceiver extends BroadcastReceiver {
    static final String SCHEDULE_ALARM_ACTION = AlarmReceiver.class.getName() + ".action.SCHEDULE_ALARM";
    static final String RESET_ALARM_ACTION = AlarmReceiver.class.getName() + ".action.RESET_ALARM";
    private static final String NUMBER_OF_FAILURES_EXTRA = AlarmReceiver.class.getName() + ".extra.NUMBER_OF_FAILURES";
    private static final String ALARM_WAS_CANCELED_EXTRA = AlarmReceiver.class.getName() + ".extra.ALARM_WAS_CANCELED";
    private static final int UNKNOWN_NUMBER_OF_FAILURES = -128;
    private static final long INITIAL_BACKOFF = TimeUnit.SECONDS.toMillis(30);
    private static final long MAX_BACKOFF = TimeUnit.MINUTES.toMillis(15);

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(MainActivity.APP_TAG, "onReceive() intent = [" + intent + "]");
        if (SCHEDULE_ALARM_ACTION.equals(intent.getAction())) {
            handleScheduleAlarmAction(context, intent);
        } else {
            handleResetAlarmAction(context);
        }
    }


    private void handleResetAlarmAction(Context context) {
        Intent intent = new Intent(SCHEDULE_ALARM_ACTION); // actions must be equals
        intent.putExtra(ALARM_WAS_CANCELED_EXTRA, true);
        PendingIntent pendingIntent = createPendingIntent(context, intent);
        getAlarmManager(context).cancel(pendingIntent);
    }

    private void handleScheduleAlarmAction(Context context, Intent intent) {
        boolean alarmWasCanceled = intent.getBooleanExtra(ALARM_WAS_CANCELED_EXTRA, false);
        Log.d(MainActivity.APP_TAG, "Was alarm canceled " + alarmWasCanceled);
        if (!alarmWasCanceled) { // if alarm wasn't canceled
            if (hasConnection(context)) {
                new JobForAlarmManager().enqueueWork(context, AlarmBasedJobLauncher.JOB_ID);
            } else {
                Log.d(MainActivity.APP_TAG, "Don't have connection. Reschedule ");
                rescheduleItself(context, intent);
            }
        }
    }

    private boolean hasConnection(Context context) {
        NetworkInfo networkInfo = getNetworkInfo(context);
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    @Nullable
    private NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm == null ? null : cm.getActiveNetworkInfo();
    }

    private void rescheduleItself(Context context, Intent intent) {
        int numberOfFailures = extractNumberOfFailures(intent);
        long delay = calculateDelay(numberOfFailures);
        numberOfFailures = increaseFailuresIfNeed(delay, numberOfFailures);
        Log.d(MainActivity.APP_TAG, "AlarmReceiver. new number of failures = [" + numberOfFailures + "]");
        intent.putExtra(NUMBER_OF_FAILURES_EXTRA, numberOfFailures);
        intent.putExtra(ALARM_WAS_CANCELED_EXTRA, false);
        PendingIntent pendingIntent = createPendingIntent(context, intent);
        Log.d(MainActivity.APP_TAG, "AlarmReceiver. Reschedule with delay = [" + delay + "], new number of failures = [" + numberOfFailures + "]");
        getAlarmManager(context).set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delay, pendingIntent);

    }

    private AlarmManager getAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    private int extractNumberOfFailures(Intent intent) {
        int numberOfFailures = intent.getIntExtra(NUMBER_OF_FAILURES_EXTRA, UNKNOWN_NUMBER_OF_FAILURES);
        return numberOfFailures == UNKNOWN_NUMBER_OF_FAILURES ? 1 : numberOfFailures;
    }

    private PendingIntent createPendingIntent(Context context, Intent intent) {
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private long calculateDelay(int numberOfFailures) {
        return (long) (INITIAL_BACKOFF * Math.pow(2, numberOfFailures - 1.0));
    }

    private int increaseFailuresIfNeed(long delay, int numberOfFailures) {
        if (delay < MAX_BACKOFF) {
            return ++numberOfFailures;
        } else return numberOfFailures;
    }

}
