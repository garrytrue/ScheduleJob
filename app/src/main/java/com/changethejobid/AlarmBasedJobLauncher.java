package com.changethejobid;

import android.app.AlarmManager;

/**
 * @author itorba
 */

public class AlarmBasedJobLauncher implements JobLauncher {
    private final AlarmManager alarmManager;

    public AlarmBasedJobLauncher(AlarmManager alarmManager) {
        this.alarmManager = alarmManager;
    }

    @Override

    public void launchJob() {

    }
}
