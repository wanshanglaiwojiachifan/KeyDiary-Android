package com.xeodou.keydiary.activity;

import group.pals.android.lib.ui.lockpattern.LockPatternActivity;
import group.pals.android.lib.ui.lockpattern.prefs.SecurityPrefs;

import com.crashlytics.android.Crashlytics;
import com.xeodou.keydiary.Config;
import com.xeodou.keydiary.MyApplication;
import com.xeodou.keydiary.R;
import com.xeodou.keydiary.UIHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class LoadingActivity extends Activity {

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
                char[] savedPattern = SecurityPrefs
                        .getPattern(getApplicationContext());;
                if (savedPattern != null) {                
                    Intent intent = new Intent(
                            LockPatternActivity.ACTION_COMPARE_PATTERN, null,
                            LoadingActivity.this, LockPatternActivity.class);
                    intent.putExtra(LockPatternActivity.EXTRA_PATTERN,
                            savedPattern);
                    startActivityForResult(intent, Config.LOCK_CODE_REQ);
                } else {
                    next();
                }
            }
        }, 2000);
    }

    private void next() {
        Intent intent = new Intent(Config.ACTION_LOADING);
        intent.setClass(LoadingActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch (requestCode) {
        case Config.LOCK_CODE_REQ: {
            /*
             * NOTE that there are 3 possible result codes!!!
             */
            switch (resultCode) {
            case RESULT_OK:
                // The user passed
                next();
                break;
            case RESULT_CANCELED:
                // The user cancelled the task
                finish();
                break;
            case LockPatternActivity.RESULT_FAILED:
                // The user failed to enter the pattern
                break;
            }

            /*
             * In any case, there's always a key EXTRA_RETRY_COUNT, which holds
             * the number of tries that the user did.
             */
            int retryCount = data.getIntExtra(
                    LockPatternActivity.EXTRA_RETRY_COUNT, 0);
            if(retryCount >= 3) {
                Toast.makeText(LoadingActivity.this, "您输入的错误次数太多，程序即将关闭。", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        finish();
                    }
                }, 2000);
            }
            break;
        }// REQ_ENTER_PATTERN
        }
    }

}
