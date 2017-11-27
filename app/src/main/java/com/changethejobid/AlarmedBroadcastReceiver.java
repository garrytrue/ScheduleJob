package com.changethejobid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * This receiver listens two actions. First is {@link AlarmBasedJobLauncher#ACTION} for determine when it needs to check network.
 * Second is {@link AlarmBasedJobLauncher#CONNECTIVITY_CHANGE_ACTION}
 *
 * @author itorba
 */

public class AlarmedBroadcastReceiver extends BroadcastReceiver {
    static final String ACTION = AlarmedBroadcastReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(MainActivity.APP_TAG, "onReceive() with Action" + intent.getAction());
        NetworkInfo networkInfo = getNetworkInfo(context);
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.d(MainActivity.APP_TAG, "onReceive() Network available");
            context.startService(new Intent(context, WorkingService.class));
            sendNetworkAvailableNotification(context);
        } else {
            Log.d(MainActivity.APP_TAG, "AlarmedBroadcastReceiver fire update delay intent");
            sentNoAvailableNetworkNotification(context);
        }
    }

    @Nullable
    private NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm == null ? null : cm.getActiveNetworkInfo();
    }

    private void sentNoAvailableNetworkNotification(Context context) {
        Intent intent = new Intent(AlarmBasedJobLauncher.ACTION);
        intent.putExtra(AlarmBasedJobLauncher.IS_DOWNLOADING_WAS_STARTED_EXTRA, false);
        context.sendBroadcast(intent);
    }

    private void sendNetworkAvailableNotification(Context context) {
        Intent intent = new Intent(AlarmBasedJobLauncher.ACTION);
        intent.putExtra(AlarmBasedJobLauncher.IS_DOWNLOADING_WAS_STARTED_EXTRA, true);
        context.sendBroadcast(intent);
    }
}
