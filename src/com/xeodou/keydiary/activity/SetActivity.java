package com.xeodou.keydiary.activity;

import java.util.Calendar;

import com.doomonafireball.betterpickers.BetterPickerUtils;
import com.doomonafireball.betterpickers.timepicker.TimePickerDialogFragment.TimePickerDialogHandler;
import com.umeng.analytics.MobclickAgent;
import com.xeodou.keydiary.Config;
import com.xeodou.keydiary.KeyAlarm;
import com.xeodou.keydiary.R;
import com.xeodou.keydiary.UIHelper;
import com.xeodou.keydiary.Utils;
import com.xeodou.keydiary.UIHelper.ToastStyle;
import com.xeodou.keydiary.database.DBUtils;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

public class SetActivity extends FragmentActivity implements OnClickListener, OnLongClickListener, TimePickerDialogHandler {

    
    private Button logoutBtn;
    private View backBtn, notiBtn, pricyView, feedbackView;
    private TextView textView, alermTime, version;
    private TimePicker timePicker;
    private boolean isAlarm = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_layout);
        
        textView = (TextView)findViewById(R.id.user);
        version = (TextView)findViewById(R.id.version);
        alermTime = (TextView) findViewById(R.id.alert_time);
        notiBtn = (View) findViewById(R.id.noti_btn);
        backBtn = (View) findViewById(R.id.back_btn);
        pricyView = (View) findViewById(R.id.pricy);
        feedbackView = (View) findViewById(R.id.feedback);
        logoutBtn = (Button) findViewById(R.id.logout_btn);        
        notiBtn.setOnClickListener(this);
        notiBtn.setOnLongClickListener(this);
        backBtn.setOnClickListener(this);
        pricyView.setOnClickListener(this);
        feedbackView.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        Utils.isLogin(this);
        textView.setText(Config.username);
        String str = (new Utils()).getAlerm(this);
        if(str != null && !str.equals("")){
            isAlarm = true;
            alermTime.setText("每日 " + str + " 提醒");
        }
        String versionName;
        try {
            versionName = getPackageManager().getPackageInfo(getApplication().getPackageName(), PackageManager.GET_ACTIVITIES).versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            versionName = "0.16";
        }
        version.setText("版本 " + versionName);
    }
//    change to new dialog
//    private void initDialog(){
//        
//        dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        View view = LayoutInflater.from(this).inflate(R.layout.pickerdialog_layout, null);
//        dialog.addContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//        timePicker = (TimePicker)view.findViewById(R.id.time_picker);
//        cancelBtn = (Button)view.findViewById(R.id.dialog_cancel);
//        okBtn = (Button)view.findViewById(R.id.dialog_ok);
//        cancelBtn.setOnClickListener(this);
//        okBtn.setOnClickListener(this);
//        timePicker.setIs24HourView(true);
//        timePicker.setCurrentHour(Utils.getCurrentHour());
//        timePicker.setCurrentMinute(Utils.getCurrentMin());
//    }
    
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
        case R.id.back_btn:
            finish();
            break;

        case R.id.logout_btn:
            if(!DBUtils.clearTables(this)){
                UIHelper.show(SetActivity.this, "清除数据失败！请重试！", ToastStyle.Alert);
                return;
            }
            (new Utils()).storePass(SetActivity.this, "", "");
            Config.username = "";
            Config.password = "";
            Intent intent = new Intent(SetActivity.this, LoginActivity.class);
            intent.setAction(Config.ACTION_SET);
            startActivity(intent);
            setResult(Config.LOGOUT_CODE);
            finish();
            break;
        case R.id.noti_btn:
//            if(alermTime.getVisibility() == View.GONE){
//                dialog.show();
                BetterPickerUtils.showTimeEditDialog(getSupportFragmentManager(), R.style.BetterPickersDialogFragment);
//            }
            break;
        case R.id.dialog_cancel:
            break;
        case R.id.feedback:
            intent = new Intent(SetActivity.this, FeedbackActivity.class);
            intent.setAction(Config.ACTION_FEEDBACK);
            startActivity(intent);
            break;
        case R.id.dialog_ok:
            int hour = timePicker.getCurrentHour();
            int min = timePicker.getCurrentMinute();
            alermTime.setText("每日 " + hour+":"+ Utils.douInt(min) + " 提醒");
            setAlerm(hour, min);
            (new Utils()).storeAlerm(SetActivity.this, hour+":"+ Utils.douInt(min));
            break;
        case R.id.pricy:
            intent = new Intent(Config.ACTION_SET);
            intent.setClass(SetActivity.this, WebActivity.class);
            intent.putExtra("URL", "http://api.keydiary.net/app/static/android/copyright");
            startActivity(intent);
            break;
        }
    }
    
    private void setAlerm(int h, int m){
        KeyAlarm.setAlarm(h, m, this);
        UIHelper.show(SetActivity.this, "设置每日提醒成功！", ToastStyle.Confirm);

        isAlarm = true;
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
        if(!isAlarm) return true;
        isAlarm = !isAlarm;
        delAlarm();
        (new Utils()).storeAlerm(SetActivity.this, "");
        UIHelper.show(SetActivity.this, "删除每日提醒成功！", ToastStyle.Confirm);

        alermTime.setText("点击设置提醒");
        return true;

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

    @Override
    public void onDialogTimeSet(int hourOfDay, int minute) {
        // TODO Auto-generated method stub
        alermTime.setText("每日 " + Utils.douInt(hourOfDay)+":"+ Utils.douInt(minute) + " 提醒");
        setAlerm(hourOfDay, minute);
        (new Utils()).storeAlerm(SetActivity.this, Utils.douInt(hourOfDay)+":"+ Utils.douInt(minute));
    }
}
