package com.changethejobid.network;

/**
 * @author itorba
 */

class DownloadableResult {
    private final boolean isOk;
    private final String id;

    DownloadableResult(boolean isOk, String id) {
        this.isOk = isOk;
        this.id = id;
    }

    boolean isOk() {
        return isOk;
    }

    String getId() {
        return id;
    }
}
