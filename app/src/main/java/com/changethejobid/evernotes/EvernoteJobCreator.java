package com.changethejobid.evernotes;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/**
 * @author itorba
 */

public class EvernoteJobCreator implements JobCreator {
    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        if (tag.equals(EvernoteJob.TAG)) {
            return new EvernoteJob();
        }
        return null;
    }
}
