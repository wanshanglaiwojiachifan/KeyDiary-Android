package com.xeodou.keydiary.activity;

import com.xeodou.keydiary.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DiaryReciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Notification nf = new Notification(R.drawable.ic_launcher, "亲！记日记的时间到了。", System.currentTimeMillis());
        nf.defaults = Notification.DEFAULT_ALL;
        nf.flags = Notification.FLAG_AUTO_CANCEL;
        Intent i = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, Intent.FLAG_ACTIVITY_CLEAR_TOP);
        nf.setLatestEventInfo(context, "关键字日记提醒", "亲！记日记的时间到了。", pendingIntent);
        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, nf);
    }

}
