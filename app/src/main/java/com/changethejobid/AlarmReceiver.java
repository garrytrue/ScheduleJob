package com.changethejobid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * This receiver listens {@link AlarmReceiver#SCHEDULE_ALARM_ACTION} for schedule alarm
 *
 * @author itorba
 */

public class AlarmReceiver extends BroadcastReceiver {
    static final String SCHEDULE_ALARM_ACTION = AlarmReceiver.class.getName() + ".action.SCHEDULE_ALARM";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(MainActivity.APP_TAG, "AlarmReceiver onReceive() intent = [" + intent + "]");
        new JobForAlarmManager().enqueueWorkWithData(context,
                AlarmBasedJobLauncher.JOB_ID,
                intent.getIntExtra(JobForAlarmManager.ATTEMPTS_EXTRA, JobForAlarmManager.DEFAULT_VALUE));
    }
}
