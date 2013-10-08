package com.xeodou.keydiary.activity;

import org.json.JSONObject;

import group.pals.android.lib.ui.lockpattern.LockPatternActivity;
import group.pals.android.lib.ui.lockpattern.prefs.SecurityPrefs;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.xeodou.keydiary.Config;
import com.xeodou.keydiary.KeyDiaryResult;
import com.xeodou.keydiary.Log;
import com.xeodou.keydiary.MyApplication;
import com.xeodou.keydiary.R;
import com.xeodou.keydiary.UIHelper;
import com.xeodou.keydiary.Utils;
import com.xeodou.keydiary.UIHelper.ToastStyle;
import com.xeodou.keydiary.bean.LoadUser;
import com.xeodou.keydiary.bean.Result;
import com.xeodou.keydiary.http.API;
import com.xeodou.keydiary.views.CustomDialog;
import com.xeodou.keydiary.views.CustomDialog.onDialogInfoConfirmListener;
import com.xeodou.keydiary.views.EditDialog;
import com.xeodou.keydiary.views.EditDialog.ClickType;
import com.xeodou.keydiary.views.EditDialog.onDialogClickListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

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
                // 用密码找回
                CustomDialog customDialog = new CustomDialog(LoadingActivity.this);
                customDialog.setDialogTitle("忘记安全密码");
                customDialog.setDialogInfo("输入安全密码错误超过5次，\n是否通过密码找回？");
                customDialog.setLeftBtnText("算了");
                customDialog.setRightBtnText("确定");
                customDialog.setOnDialogInfoConfirmListener(new onDialogInfoConfirmListener() {
                    
                    @Override
                    public void onClick(View v, ClickType type) {
                        // TODO Auto-generated method stub
                        if(type.equals(ClickType.Ok)){
                            editDialog = new EditDialog(LoadingActivity.this);
                            editDialog.getTitleText().setText("验证密码");
                            editDialog.setEditHint("请输入密码");                         
                            editDialog.setEditContent("");
                            editDialog.getEditContent().setTransformationMethod(PasswordTransformationMethod.getInstance());
                            editDialog.setOnDialogClickListener(new onDialogClickListener() {

                                @Override
                                public void onClick(ClickType type, final String content,
                                        final String day, View v) {
                                    // TODO Auto-generated method stub
                                    if(!type.equals(ClickType.Delete)){
                                        if (!Utils.isLogin(LoadingActivity.this)) {
                                            clearSafePass();
                                            next();
                                            return;
                                        }
                                        if (content.length() <= 0) return ;
                                        Config.password = content;
                                        login();
                                    } else {
                                        closeSlef();
                                    }
                                }
                            });
                            editDialog.show();
                       
                        } else {
                            closeSlef();
                        }
                    }
                });
                customDialog.show();
                
            }
            break;
        }// REQ_ENTER_PATTERN
        }
    }
    
    public void clearSafePass() {
        SecurityPrefs.setPattern(getApplicationContext(), null);
    }
    
    public void login() {
        API.getUser(new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(JSONObject response) {
                // TODO Auto-generated method stub

                Gson gson = new Gson();
                LoadUser user = gson.fromJson(response.toString(), LoadUser.class);
                if (user.getStat() == 1 && user != null) {
//                    (new Utils()).storeUser(LoginActivity.this, user.getData());
                    sendMsg(Config.SUCCESSS_CODE, null);
                }
            }

            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
                // TODO Auto-generated method stub
                Gson gson = new Gson();
                try {
                    Result result = gson.fromJson(errorResponse.toString(), Result.class);
                    if(result == null) {
                        sendMsg(Config.FAIL_CODE, "未知问题");
                        return;
                    }
                    sendMsg(Config.FAIL_CODE, KeyDiaryResult.getMsg(result.getStat()));
                } catch (JsonSyntaxException e1) {
                    // TODO Auto-generated catch block
                    sendMsg(Config.FAIL_CODE, "登录失败");
                }
            }
           
            @Override
            public void onFailure(Throwable error, String content) {
                // TODO Auto-generated method stub
                sendMsg(Config.FAIL_CODE, content);
            }

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                showDialog(getResources().getString(R.string.loading));
            }
           
        });
    }
    
    private void closeSlef() {
        Toast.makeText(LoadingActivity.this, "程序即将关闭。", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            
            @Override
            public void run() {
                // TODO Auto-generated method stub
                finish();
            }
        }, 2000);
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
    
    private void sendMsg(int code , Object obj){
        Message msg = new Message();
        msg.what = code;
        msg.obj = obj;
        handler.sendMessage(msg);
    }
    
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if(msg.obj != null){
                UIHelper.show(LoadingActivity.this, msg.obj.toString(), ToastStyle.Alert);
            }
            dismissDialog();
            
            switch (msg.what) {
            case Config.SUCCESSS_CODE:
                if(editDialog != null) editDialog.dismiss();
                UIHelper.show(LoadingActivity.this, "验证成功正在跳转", ToastStyle.Info);
                clearSafePass();
                next();
                break;
            case Config.FAIL_CODE:
                
                
                break;
            default:
                break;
            }
            
        }
        
    };

}
