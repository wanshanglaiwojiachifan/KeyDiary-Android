package com.xeodou.keydiary.activity;

import com.xeodou.keydiary.Config;
import com.xeodou.keydiary.R;
import com.xeodou.keydiary.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SetActivity extends Activity implements OnClickListener {

    
    private Button backBtn, logoutBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_layout);
        
        backBtn = (Button) findViewById(R.id.back_btn);
        logoutBtn = (Button) findViewById(R.id.logout_btn);
        
        backBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
        case R.id.back_btn:
            finish();
            break;

        case R.id.logout_btn:
            (new Utils()).storePass(SetActivity.this, "", "");
            Intent intent = new Intent(SetActivity.this, LoginActivity.class);
            startActivity(intent);
            setResult(Config.LOGOUT_CODE);
            finish();
            break;
        }
    }

}
