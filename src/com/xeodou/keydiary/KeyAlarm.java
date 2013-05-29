package com.xeodou.keydiary;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.xeodou.keydiary.activity.DiaryReciver;

public class KeyAlarm {

    public static void setAlarm(int h, int m, Context context){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int ch = calendar.get(Calendar.HOUR_OF_DAY); 
        int cm = calendar.get(Calendar.MINUTE);
        int r = 0;
        if(h > ch) {
                r = (h - ch) * 60 + (m - cm);
        } else if(h < ch) {
                r = (h + 24 - ch) * 60 + (m - cm);
        } else {
            r = m >= cm ? m - cm : 24 * 60 + m - cm;
        }
        r = r * 60;
        calendar.add(Calendar.SECOND, r);
        Intent intent = new Intent(context, DiaryReciver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, Config.ALERM_ID , intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
    }
}
