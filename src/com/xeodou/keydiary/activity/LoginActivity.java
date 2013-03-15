package com.xeodou.keydiary.activity;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.xeodou.keydiary.Config;
import com.xeodou.keydiary.Log;
import com.xeodou.keydiary.R;
import com.xeodou.keydiary.Utils;
import com.xeodou.keydiary.bean.LoadUser;
import com.xeodou.keydiary.http.API;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

public class LoginActivity extends Activity implements OnClickListener{

    private final String TAG = "LoginActivity";
    private EditText username;
    private EditText password;
    private Button button;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        username = (EditText)findViewById(R.id.username_etv);
        password = (EditText)findViewById(R.id.password_etv);
        button = (Button)findViewById(R.id.login_btn);
        button.setOnClickListener(this);
        username.setSelected(true);
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if(username.getText().length() > 1 && password.getText().length() > 1){
            Config.username = username.getText().toString();
            Config.password = password.getText().toString();
            API.getUser(new JsonHttpResponseHandler(){

                @Override
                public void onSuccess(JSONObject response) {
                    // TODO Auto-generated method stub
                    Log.d(TAG, response.toString());
                    Gson gson = new Gson();
                    LoadUser user = gson.fromJson(response.toString(), LoadUser.class);
                    if (user.getStat() == 1 && user != null) {
                        sendMsg(Config.SUCCESSS_CODE, null);
                        (new Utils()).storeUser(LoginActivity.this, user.getData());
                    }
                }

                @Override
                public void onFailure(Throwable e, JSONObject errorResponse) {
                    // TODO Auto-generated method stub
                    Log.d(TAG, errorResponse.toString() + "  " + e.getMessage());
                    sendMsg(Config.FAIL_CODE, e.toString());
                }

                @Override
                public void onStart() {
                    // TODO Auto-generated method stub
                    showDialog(getResources().getString(R.string.loading));
                }

                @Override
                public void onFinish() {
                    // TODO Auto-generated method stub
                    dismissDialog();
                }
               
            });
        }
    }
    
    private void showDialog(String msg){
        if (dialog == null) {
            dialog = new ProgressDialog(this);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.setMessage(msg);
        }
        dialog.show();
    }
    
    private void dismissDialog(){
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
    
    private void sendMsg(int code, String str){
        Message msg = new Message();
        msg.what = code;
        msg.obj = str;
        handler.sendMessage(msg);
    }
    
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
            case Config.SUCCESSS_CODE:
                (new Utils()).storePass(LoginActivity.this, username.getText().toString(), password.getText().toString());
                setResult(Config.LOGIN_CODE);
                finish();
                break;
            }
            dismissDialog();
        }
       
    };
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            setResult(Config.FAIL_CODE);
        }
        return super.onKeyDown(keyCode, event);
    }
}
