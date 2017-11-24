package com.changethejobid;

import android.content.Context;

/**
 * @author itorba
 */

public interface MainContract {
    interface MainView {

        Context getAppContext();

        String getStartJobTime();

        String getEndJobTime();

        void showError();
    }

    interface MainPresenter {
        void bindView(MainView view);

        void onScheduleClicked();

        void unbindView();
    }
}
