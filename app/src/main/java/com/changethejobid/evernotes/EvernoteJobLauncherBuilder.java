package com.changethejobid.evernotes;

import android.content.Context;

import com.changethejobid.JobLauncher;
import com.changethejobid.JobLauncherBuilder;

/**
 * @author itorba
 */

public class EvernoteJobLauncherBuilder extends JobLauncherBuilder {
    public EvernoteJobLauncherBuilder(Context context) {
        super(context);
    }

    @Override
    public JobLauncher build() {
        return new EvernoteJobLauncher();
    }
}
