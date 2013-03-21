package com.xeodou.keydiary.activity;

import java.util.Calendar;

import com.umeng.analytics.MobclickAgent;
import com.xeodou.keydiary.Config;
import com.xeodou.keydiary.R;
import com.xeodou.keydiary.Utils;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

public class SetActivity extends Activity implements OnClickListener, OnLongClickListener {

    
    private Button backBtn, logoutBtn, notiBtn;
    private TextView textView, alermTime;
    private boolean isShow = false;
    private Dialog dialog;
    private TimePicker timePicker;
    private Button cancelBtn, okBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_layout);
        
        textView = (TextView)findViewById(R.id.user);
        alermTime = (TextView) findViewById(R.id.alert_time);
        alermTime.setVisibility(View.GONE);
        notiBtn = (Button) findViewById(R.id.noti_btn);
        backBtn = (Button) findViewById(R.id.back_btn);
        logoutBtn = (Button) findViewById(R.id.logout_btn);        
        notiBtn.setOnClickListener(this);
        notiBtn.setOnLongClickListener(this);
        backBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        textView.setText(Config.username);
        initDialog();
        String str = (new Utils()).getAlerm(this);
        if(str != null && !str.equals("")){
            alermTime.setText(str);
            alermTime.setVisibility(View.VISIBLE);
            notiBtn.setBackgroundResource(R.drawable.set_noti_y);
        }
    }
    
    private void initDialog(){
        
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(this).inflate(R.layout.pickerdialog_layout, null);
        dialog.addContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        timePicker = (TimePicker)view.findViewById(R.id.time_picker);
        cancelBtn = (Button)view.findViewById(R.id.dialog_cancel);
        okBtn = (Button)view.findViewById(R.id.dialog_ok);
        cancelBtn.setOnClickListener(this);
        okBtn.setOnClickListener(this);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(Utils.getCurrentHour());
        timePicker.setCurrentMinute(Utils.getCurrentMin());
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
        case R.id.noti_btn:
//            if(alermTime.getVisibility() == View.GONE){
                isShow = true;
                dialog.show();
//            }
            break;
        case R.id.dialog_cancel:
            if(dialog.isShowing())dialog.cancel();
            break;
        case R.id.dialog_ok:
            int hour = timePicker.getCurrentHour();
            int min = timePicker.getCurrentMinute();
            alermTime.setText(hour+":"+min);
            alermTime.setVisibility(View.VISIBLE);
            setAlerm(hour, min);
            (new Utils()).storeAlerm(SetActivity.this, hour+":"+min);
            notiBtn.setBackgroundResource(R.drawable.set_noti_y);
            dialog.cancel();
            break;
        }
    }
    
    private void setAlerm(int h, int m){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, h);
        calendar.set(Calendar.MINUTE, m);
        Intent intent = new Intent(this, DiaryReciver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, Config.ALERM_ID , intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
    }
    
    private void delAlarm(){
       Intent intent = new Intent(this, DiaryReciver.class);
       PendingIntent pi = PendingIntent.getBroadcast(this, Config.ALERM_ID, intent, 0);
       AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
       am.cancel(pi);
    }
    
    @Override
    public boolean onLongClick(View v) {
        // TODO Auto-genetrated method stub
        if(alermTime.getVisibility() == View.GONE){
//            isShow = true;
//            dialog.show();
        } else {
            delAlarm();
            alermTime.setVisibility(View.GONE);
            (new Utils()).storeAlerm(SetActivity.this, "");
            Crouton.showText(SetActivity.this, "删除每日提醒成功！", Style.CONFIRM);
            notiBtn.setBackgroundResource(R.drawable.set_noti_n);
        }
        return false;
    }
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
}
