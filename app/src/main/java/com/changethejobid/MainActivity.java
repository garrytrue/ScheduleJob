package com.changethejobid;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements MainContract.MainView {
    public static final String APP_TAG = "MainActivity";
    private EditText etStartWindow;
    private EditText etEndWindow;
    private View root;
    private MainContract.MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        presenter = new PresenterMainActivity();
        presenter.bindView(this);
    }

    private void initUI() {
        root = findViewById(R.id.root);
        etStartWindow = findViewById(R.id.startWindowTime);
        etEndWindow = findViewById(R.id.endWindowTime);
        findViewById(R.id.btn_schedule_job).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onScheduleClicked();
            }
        });
    }

    @Override
    public Context getAppContext() {
        return this.getApplicationContext();
    }

    @Override
    public String getStartJobTime() {
        return etStartWindow.getText().toString();
    }

    @Override
    public String getEndJobTime() {
        return etEndWindow.getText().toString();
    }

    @Override
    public void showError() {
        Snackbar.make(root, R.string.error_input, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unbindView();
    }
}
