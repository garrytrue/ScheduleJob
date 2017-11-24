package com.changethejobid;

import android.app.AlarmManager;
import android.app.PendingIntent;

/**
 * @author itorba
 */

class AlarmBasedJobLauncher implements JobLauncher {
    private final AlarmManager alarmManager;
    private final PendingIntent pendingIntent;

    AlarmBasedJobLauncher(AlarmManager alarmManager, PendingIntent pendingIntent) {
        this.alarmManager = alarmManager;
        this.pendingIntent = pendingIntent;
    }

    @Override

    public void launchJob() {
    }
}
