package com.xeodou.keydiary.activity;

import com.crashlytics.android.Crashlytics;
import com.xeodou.keydiary.Config;
import com.xeodou.keydiary.R;
import com.xeodou.keydiary.views.EditDialog;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LoadingActivity extends Activity {

    private ProgressDialog dialog;
    private EditDialog editDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        Crashlytics.start(this);
        setContentView(R.layout.activity_loading);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
              next();
            }
        }, 2000);
    }

    private void next() {
        Intent intent = new Intent(Config.ACTION_LOADING);
        intent.setClass(LoadingActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
