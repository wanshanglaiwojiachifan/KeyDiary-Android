package com.xeodou.keydiary.activity;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;
import com.xeodou.keydiary.Config;
import com.xeodou.keydiary.KeyDiaryResult;
import com.xeodou.keydiary.Log;
import com.xeodou.keydiary.R;
import com.xeodou.keydiary.Utils;
import com.xeodou.keydiary.bean.LoadUser;
import com.xeodou.keydiary.bean.Result;
import com.xeodou.keydiary.http.API;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class LoginActivity extends Activity implements OnClickListener, OnEditorActionListener, OnKeyListener{

    private final String TAG = "LoginActivity";
    private EditText username;
    private EditText password;
    private Button button;
    private ProgressDialog dialog;
    private String action;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        action = getIntent().getAction();
        username = (EditText)findViewById(R.id.username_etv);
        password = (EditText)findViewById(R.id.password_etv);
        button = (Button)findViewById(R.id.login_btn);
        button.setOnClickListener(this);
        username.setSelected(true);
        password.setOnEditorActionListener(this);
        password.setOnKeyListener(this);
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        login();
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
    
    private void login(){
        Config.username = Config.password = "";
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
//                        (new Utils()).storeUser(LoginActivity.this, user.getData());
                        sendMsg(Config.SUCCESSS_CODE, null);
                    }
                }

                @Override
                public void onFailure(Throwable e, JSONObject errorResponse) {
                    // TODO Auto-generated method stub
                    Log.d(TAG, errorResponse.toString() + "  " + e.getMessage());
                    Gson gson = new Gson();
                    Result result = gson.fromJson(errorResponse.toString(), Result.class);
                    sendMsg(Config.FAIL_CODE, KeyDiaryResult.getMsg(result.getStat()));
                }
               
                @Override
                public void onFailure(Throwable error, String content) {
                    // TODO Auto-generated method stub
                    super.onFailure(error, content);
                }

                @Override
                public void onStart() {
                    // TODO Auto-generated method stub
                    showDialog(getResources().getString(R.string.loading));
                }
               
            });
        } else {
            if(username.getText().toString().length() <= 0){
                Crouton.showText(this, "邮箱不能为空", Style.ALERT);
                return;
            }
            if(password.getText().toString().length() <= 0){
                Crouton.showText(this, "密码不能为空", Style.ALERT);
                return;
            }
        }
    }
    
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            dismissDialog();
            switch (msg.what) {
            case Config.SUCCESSS_CODE:
                (new Utils()).storePass(LoginActivity.this, username.getText().toString(), password.getText().toString());
                if(action != null && action.equals(Config.ACTION_SET)){
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this, MainActivity.class);
                    intent.setAction(Config.ACTION_LOGIN);
                    startActivity(intent);
                }
                setResult(Config.LOGIN_CODE);
                finish();
                break;
            }
            if(msg.obj != null){
                Crouton.showText(LoginActivity.this, msg.obj.toString(), Style.ALERT);
            }
        }
       
    };
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MobclickAgent.onResume(this);
    }
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        // TODO Auto-generated method stub
        if(actionId == EditorInfo.IME_ACTION_DONE){
            login();
            return  false;
        }
        return false;
    }
    @Override
    public boolean onKey(View arg0, int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN){
            login();
            return false;
        }
        return false;
    }
}
