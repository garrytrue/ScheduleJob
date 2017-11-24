package com.changethejobid;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.Job;

import static com.firebase.jobdispatcher.Constraint.ON_ANY_NETWORK;

/**
 * @author itorba
 */

public class JobLauncherApi20Below implements JobLauncher {
    private static final String JOB_TAG = JobLauncherApi20Below.class.getName();
    private final FirebaseJobDispatcher dispatcher;
    private final Job job;

    public JobLauncherApi20Below(FirebaseJobDispatcher dispatcher) {
        this.dispatcher = dispatcher;
        job = createJob(this.dispatcher);
    }

    @Override
    public void launchJob() {
        dispatcher.schedule(job);
    }

    private Job createJob(FirebaseJobDispatcher jobDispatcher) {
        return jobDispatcher.newJobBuilder()
                .setService(JobApi20Below.class)
                .setTag(JOB_TAG)
                .setConstraints(ON_ANY_NETWORK)
                .build();
    }
}
