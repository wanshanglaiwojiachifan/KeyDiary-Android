package com.xeodou.keydiary.activity;

import com.xeodou.keydiary.KeyAlarm;
import com.xeodou.keydiary.Log;
import com.xeodou.keydiary.Utils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getAction();
        Log.i("Boot", action + " ----- " + "boot"); 
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            String str = (new Utils()).getAlerm(context);
            if(str == null || str.length() <= 0) return;
            String[] strs = str.split(":");
            String hs = strs[0];
            String ms = strs[1];
            Log.e("Boot", Integer.parseInt(hs) + "---" + Integer.parseInt(ms) + "----" +str); 
            KeyAlarm.setAlarm(Integer.parseInt(hs), Integer.parseInt(ms), context);
        }

    }

}
