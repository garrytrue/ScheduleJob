package com.changethejobid.evernotes;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.changethejobid.WorkingService;
import com.evernote.android.job.Job;

/**
 * @author itorba
 */

public class EvernoteJob extends Job {
    static final String TAG = "EvernoteJob";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        getContext().getApplicationContext().startService(new Intent(getContext().getApplicationContext(), WorkingService.class));
        return Result.SUCCESS;
    }
}
