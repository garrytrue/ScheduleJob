package com.changethejobid;

import android.util.Log;

import com.changethejobid.evernotes.EvernoteJobLauncherBuilder;


/**
 * @author itorba
 */

public class PresenterMainActivity implements MainContract.MainPresenter {
    private MainContract.MainView view;
    private long startMs;
    private long stopMs;
    private JobLauncher jobLauncher;

    @Override
    public void bindView(MainContract.MainView view) {
        this.view = view;
//        jobLauncher = new JobLauncherBuilder(this.view.getAppContext()).build();
        // testing new evernote lib
        jobLauncher = new EvernoteJobLauncherBuilder(this.view.getAppContext()).build();
    }

    @Override
    public void onScheduleClicked() {
        if (isTimingChainValid()) {
            Log.d(MainActivity.APP_TAG, "onScheduleClicked: ");
            jobLauncher.launchJob();
        } else {
            view.showError();
        }
    }

    @Override
    public void unbindView() {
        view = null;
    }

    private boolean isTimingChainValid() {
        String stringStartMs = view.getStartJobTime();
        String stringStopMs = view.getEndJobTime();
        if (stringStartMs != null && stringStopMs != null) {
            try {
                startMs = Long.parseLong(stringStartMs);
                stopMs = Long.parseLong(stringStopMs);
                return true;
            } catch (NumberFormatException ex) {
                return false;
            }
        }
        return false;
    }

}
