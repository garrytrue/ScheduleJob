package com.changethejobid.network;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author itorba
 */

public class DownloadablesRepo {
    private final CopyOnWriteArrayList<Downloadable> downloadables = new CopyOnWriteArrayList<>();

    public DownloadablesRepo() {
        for (int i = 0; i < 5; i++) {
            // need setup result depends on connection
            downloadables.add(new Downloadable(i % 2 == 0, 60000));
        }
    }

    void removeSucceed(String id) {
        for (Downloadable downloadable : downloadables) {
            if (downloadable.getId().equals(id)) {
                downloadables.remove(downloadable);
            }
        }
    }

    boolean isAllCompletedWithSuccess() {
        // it's a last call before reschedule, so need revert results in failed jobs
        revertFailedDownloadables();
        return downloadables.isEmpty();
    }

    List<Downloadable> getDownloadables() {
        return Collections.unmodifiableList(downloadables);
    }

    private void revertFailedDownloadables() {
        for (Downloadable forRevert : downloadables) {
            forRevert.changeResultToOk();
        }
    }
}
