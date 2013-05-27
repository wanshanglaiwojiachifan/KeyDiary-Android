package com.xeodou.keydiary.activity;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.xeodou.keydiary.Config;
import com.xeodou.keydiary.KeyDiaryResult;
import com.xeodou.keydiary.KeyboardDetectorLineayLayout;
import com.xeodou.keydiary.UIHelper;
import com.xeodou.keydiary.KeyboardDetectorLineayLayout.IKeyboardChanged;
import com.xeodou.keydiary.UIHelper.ToastStyle;
import com.xeodou.keydiary.Log;
import com.xeodou.keydiary.R;
import com.xeodou.keydiary.Utils;
import com.xeodou.keydiary.bean.LoadUser;
import com.xeodou.keydiary.bean.Result;
import com.xeodou.keydiary.http.API;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class LoginActivity extends Activity implements OnClickListener, OnEditorActionListener, OnKeyListener,  IKeyboardChanged{

    private final String TAG = "LoginActivity";
    private EditText username;
    private EditText password;
    private Button button, btn_register;
    private View rela, logo;
    private KeyboardDetectorLineayLayout root;
    private ProgressDialog dialog;
    private String action;
    private boolean isInput = false;
    private int time = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.update(this);
        action = getIntent().getAction();
        username = (EditText)findViewById(R.id.username_etv);
        password = (EditText)findViewById(R.id.password_etv);
        button = (Button)findViewById(R.id.login_btn);
        rela = (View)findViewById(R.id.logind_rela);
        btn_register = (Button)findViewById(R.id.btn_rigester);
        btn_register.setText(Html.fromHtml("<u>"+"注册帐号"+"</u>"));
        logo = (View) findViewById(R.id.login_logo);
        root = (KeyboardDetectorLineayLayout) findViewById(R.id.login_root);
        btn_register.setOnClickListener(this);
        button.setOnClickListener(this);
        password.setOnEditorActionListener(this);
        password.setOnKeyListener(this);
        password.clearFocus();
        username.clearFocus();
        root.addKeyboardStateChangedListener(this);
        username.setOnClickListener(this);
        password.setOnClickListener(this);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        String action = getIntent().getAction();
        if(action != null && action.endsWith(Config.ACTION_SET)){
            time = 2;
        }

    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
        case R.id.login_btn:
            login();
            break;
        case R.id.btn_rigester:
            Intent intent = new Intent(this, WebActivity.class);
            intent.setAction(Config.ACTION_REGISTER);
            startActivity(intent);
            break;
        }
    }
    
    private void moveUp(){
        if(isInput) return;
        LayoutParams params = (LayoutParams)logo.getLayoutParams();
        int t = params.topMargin - (int)getResources().getDimension(R.dimen.moveHeight);
        params.topMargin = t;
        logo.setLayoutParams(params);
        params = (LayoutParams)rela.getLayoutParams();
        params.topMargin = t;
        rela.setLayoutParams(params);
        isInput = true;
    }
    
    private void moveDown(){
        if(!isInput) return;
        LayoutParams params = (LayoutParams)logo.getLayoutParams();
        int t = params.topMargin + (int)getResources().getDimension(R.dimen.moveHeight);
        params.topMargin = t;
        logo.setLayoutParams(params);
        params = (LayoutParams)rela.getLayoutParams();
        params.topMargin = (int)getResources().getDimension(R.dimen.topMargin);
        rela.setLayoutParams(params);
        isInput = false;
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
                    try {
                        Result result = gson.fromJson(errorResponse.toString(), Result.class);
                        if(result == null) {
                            sendMsg(Config.FAIL_ADD, "未知问题");
                            return;
                        }
                        sendMsg(Config.FAIL_CODE, KeyDiaryResult.getMsg(result.getStat()));
                    } catch (JsonSyntaxException e1) {
                        // TODO Auto-generated catch block
                        sendMsg(Config.FAIL_ADD, "登录失败");
                    }
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
                UIHelper.show(this, "邮箱不能为空", ToastStyle.Alert);
                return;
            }
            if(password.getText().toString().length() <= 0){
                UIHelper.show(this, "密码不能为空", ToastStyle.Alert);
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
                UIHelper.show(LoginActivity.this, msg.obj.toString(), ToastStyle.Alert);

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
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            setResult(Config.FAIL_CODE);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onKeyboardShown() {
        // TODO Auto-generated method stub
        if(time != 1){
            btn_register.setVisibility(View.GONE);
            // Toast.makeText(this, "show", Toast.LENGTH_SHORT).show();
            moveUp();
        }
        time = 2;
    }
    @Override
    public void onKeyboardHidden() {
        // TODO Auto-generated method stub
        btn_register.setVisibility(View.VISIBLE);
//        Toast.makeText(this, "hidden", Toast.LENGTH_SHORT).show();
        moveDown();
    }
    
}
