package com.changethejobid;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.changethejobid.network.DownloadManager;
import com.changethejobid.network.OnDownloadsFinishedListener;

/**
 * @author itorba
 */

public class WorkingService extends Service implements OnDownloadsFinishedListener {
    private static final int SERVICE_ID = 255;
    private JobStateHolder jobStateHolder;

    @Override
    public void onCreate() {
        super.onCreate();
        jobStateHolder = ((JobsApplication) getApplication()).getJobStateHolder();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // do nothing
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        Log.d(MainActivity.APP_TAG, "onStartCommand() called with: intent = [" + intent + "], flags = [" + flags + "], startId = [" + startId + "]");
        jobStateHolder = ((JobsApplication) getApplication()).getJobStateHolder();
        if (startId == 1) { // need handle only first start
            startForeground(SERVICE_ID, new Notification.Builder(getApplicationContext()).setSmallIcon(R.drawable.ic_launcher_background).build());
            DownloadManager downloadManager = ((JobsApplication) getApplication()).getDownloadManager();
            downloadManager.setOnDownloadsFinishedListener(this);
            downloadManager.startLoadContent();
            return START_STICKY;
        } else {
            stopSelf(startId);
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(MainActivity.APP_TAG, "onDestroy() called");
        jobStateHolder.removeJob(26041983);
        super.onDestroy();
    }

    private void stopMeNow() {
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(MainActivity.APP_TAG, "Finish background job. Stop service ");
                stopSelf();
            }
        });
    }

    // TODO: 29.11.2017 Hardcoded JOB_ID
    @Override
    public void onAllDownloadFinished() {
        jobStateHolder.updateJobState(26041983, JobState.FINISHED_WITH_SUCCESS);
        stopMeNow();
    }

    @Override
    public void onFinishedWithError() {
        // need reschedule Job, and finish service
        jobStateHolder.updateJobState(26041983, JobState.FINISHED_WITH_ERROR);
        stopMeNow();
    }
}
