package com.xeodou.keydiary.activity;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.xeodou.keydiary.Config;
import com.xeodou.keydiary.R;
import com.xeodou.keydiary.http.API;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class FeedbackActivity extends Activity implements OnClickListener {

    private EditText content_ext;
    private Button back_btn, send_btn;
    private ProgressDialog dialog;
    private boolean isDone = true;
    private boolean done = false;
    private String str;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        content_ext = (EditText) findViewById(R.id.feedback_content);
        back_btn = (Button) findViewById(R.id.back_btn);
        send_btn = (Button) findViewById(R.id.send_btn);
        back_btn.setOnClickListener(this);
        send_btn.setOnClickListener(this);
    }
    
    private void feedBack(String content){
        API.feedBack(content, new AsyncHttpResponseHandler(){

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                if( dialog == null) { 
                    dialog = new ProgressDialog(FeedbackActivity.this);
                    dialog.setCancelable(true);
                    dialog.setMessage("正在提交请稍候");
                }
                dialog.show();
            }

            @Override
            public void onSuccess(String content) {
                // TODO Auto-generated method stub
                Log.d("----", content);
                if(content.contains("\"stat\": 1")){
                    sendMsg(Config.SUCCESSS_CODE, "感谢您提供的宝贵意见！");
                } else {
                    sendMsg(Config.SUCCESSS_CODE, "感谢您提供的宝贵意见！");
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                // TODO Auto-generated method stub
                sendMsg(Config.FAIL_CODE, "提交意见失败请重试");
            }
            
        });
    }
    
    private void sendMsg(int code, Object obj){
        Message msg = new Message();
        msg.what = code;
        msg.obj = obj;
        handler.sendMessage(msg);
    }

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            dialog.dismiss();
            isDone = true;
            if(msg.what == Config.SUCCESSS_CODE){
                done = true;
                Crouton.showText(FeedbackActivity.this, msg.obj.toString(), Style.CONFIRM);
                new Handler().postDelayed(new Runnable() {
                    
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        finish();
                    }
                }, 500);
            } else {
                Crouton.showText(FeedbackActivity.this, msg.obj.toString(), Style.ALERT);
            }
        }
        
    };
    
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
        case R.id.back_btn:
            finish();
            break;

        case R.id.send_btn:
            if(content_ext.getText().length() > 0){
                if(str != null && str.equals(content_ext.getText().toString()) && done){
                    Crouton.showText(FeedbackActivity.this, "相同的内容已经提交", Style.ALERT);
                    return;
                }
                if(done) {
                    Crouton.showText(FeedbackActivity.this, "您提交的内容次数过多，请等待完成后重试！", Style.ALERT);
                    return;
                }
                feedBack(content_ext.getText().toString());
                isDone = false;
                str = content_ext.getText().toString();
            }
            break;
        }
    }
}
