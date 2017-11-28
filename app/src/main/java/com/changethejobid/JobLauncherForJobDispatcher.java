package com.changethejobid;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.Job;

import static com.firebase.jobdispatcher.Constraint.ON_ANY_NETWORK;

/**
 * @author itorba
 */

class JobLauncherForJobDispatcher implements JobLauncher {
    private static final String JOB_TAG = JobLauncherForJobDispatcher.class.getName();
    private final FirebaseJobDispatcher dispatcher;
    private final Job job;

    JobLauncherForJobDispatcher(FirebaseJobDispatcher dispatcher) {
        this.dispatcher = dispatcher;
        job = createJob(this.dispatcher);
    }

    @Override
    public void launchJob() {
        dispatcher.schedule(job);
    }

    private Job createJob(FirebaseJobDispatcher jobDispatcher) {
        return jobDispatcher.newJobBuilder()
                .setService(JobForJobDispatcher.class)
                .setTag(JOB_TAG)
                .setConstraints(ON_ANY_NETWORK)
                .build();
    }
}
