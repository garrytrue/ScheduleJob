package com.changethejobid;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Class holds the current states of jobs. State is one of {@link JobState} value
 * Class is used only with *Alarm stuff and inside {@link WorkingService}
 *
 * @author itorba
 */

class JobStateHolder {
    private final ConcurrentMap<Integer, JobState> jobResultMap = new ConcurrentHashMap<>();

    void addJob(int jobId) {
        jobResultMap.putIfAbsent(jobId, JobState.PENDING);
    }

    void removeJob(int jobId) {
        jobResultMap.remove(jobId);
    }

    JobState updateJobState(int jobId, JobState newState) {
        return jobResultMap.replace(jobId, newState);
    }

    JobState getJobState(int jobId) {
        return jobResultMap.get(jobId);
    }
}
