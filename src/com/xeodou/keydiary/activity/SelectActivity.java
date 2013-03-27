package com.xeodou.keydiary.activity;

import com.xeodou.keydiary.Config;
import com.xeodou.keydiary.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SelectActivity extends Activity implements OnClickListener {

    private Button registerBtn, loginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firstpager_layout);
        
        registerBtn = (Button)findViewById(R.id.register_btn);
        loginBtn = (Button)findViewById(R.id.login_select_btn);
        
        
        registerBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
        case R.id.register_btn:
            
            break;

        case R.id.login_select_btn:
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, Config.REQ_CODE);
            break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if(resultCode == Config.LOGIN_CODE){
            setResult(resultCode);
            finish();
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            setResult(Config.FAIL_CODE);
        }
        return super.onKeyDown(keyCode, event);
    }
    
}
