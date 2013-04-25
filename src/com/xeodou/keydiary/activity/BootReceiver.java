package com.xeodou.keydiary.activity;

import java.util.Calendar;

import com.xeodou.keydiary.Config;
import com.xeodou.keydiary.MyApplication;
import com.xeodou.keydiary.Utils;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            String str = (new Utils()).getAlerm(context);
            String h = str.split("-")[0];
            String m = str.split("-")[1];
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(h));
            calendar.set(Calendar.MINUTE, Integer.parseInt(m));
            calendar.set(Calendar.SECOND, 0);
            Intent in = new Intent(context, DiaryReciver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, Config.ALERM_ID , in, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
        }

    }

}
