package com.changethejobid.network;

import android.os.SystemClock;
import android.util.Log;

import com.changethejobid.MainActivity;

import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * @author itorba
 */

public class Downloadable implements Callable<DownloadableResult> {
    private boolean isResultOk;
    private final long executionTimeMs;
    private final String id;

    public Downloadable(boolean isResultOk, long executionTime) {
        id = UUID.randomUUID().toString();
        this.isResultOk = isResultOk;
        this.executionTimeMs = executionTime;
    }

    @Override
    public String toString() {
        return "Downloadable{" + "isResultOk=" + isResultOk +
                ", executionTimeMs=" + executionTimeMs +
                ", id='" + id + '\'' +
                '}';
    }

    void changeResultToOk() {
        isResultOk = true;
    }

    String getId() {
        return id;
    }

    @Override
    public DownloadableResult call() throws Exception {
        Log.d(MainActivity.APP_TAG, "Start execute " + id);
        SystemClock.sleep(executionTimeMs);
        Log.d(MainActivity.APP_TAG, "Stop execute " + id);
        return new DownloadableResult(isResultOk, id);
    }
}
