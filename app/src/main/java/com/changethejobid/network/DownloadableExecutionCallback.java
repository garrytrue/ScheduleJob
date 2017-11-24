package com.changethejobid.network;

/**
 * @author itorba
 */

public interface DownloadableExecutionCallback {
    void onSuccess(String id);

    void onError(String id);

    void onComplete();
}
