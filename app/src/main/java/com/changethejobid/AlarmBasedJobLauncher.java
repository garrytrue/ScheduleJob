package com.changethejobid;

import android.content.Context;

/**
 * @author itorba
 */

class AlarmBasedJobLauncher implements JobLauncher {
    private final Context appContext;
    static final int JOB_ID = 26041983;

    AlarmBasedJobLauncher(Context context) {
        appContext = context;

    }

    @Override
    public void launchJob() {
        new JobForAlarmManager().enqueueWork(appContext, JOB_ID);
    }

}
