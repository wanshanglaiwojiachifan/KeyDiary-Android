package com.xeodou.keydiary.activity;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.xeodou.keydiary.Config;
import com.xeodou.keydiary.Log;
import com.xeodou.keydiary.R;
import com.xeodou.keydiary.Utils;
import com.xeodou.keydiary.bean.LoadADiary;
import com.xeodou.keydiary.http.API;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

public class DiaryReciver extends BroadcastReceiver {

    private Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        this.context = context;
        if(!Utils.isLogin(context)) return;
        API.getDiaryByDay(Utils.getFormatDayDate(), new AsyncHttpResponseHandler(){

            @Override
            public void onSuccess(String content) {
                // TODO Auto-generated method stub
                Log.d("--------", content);
                Gson gson = new Gson();
                LoadADiary result = gson.fromJson(content, LoadADiary.class);
                if(result != null & result.getStat() == 1){
                    String str = result.getData().getContent();
                    if( str == null || str.equals("") ){
                        Message msg = new Message();
                        msg.what = Config.SUCCESSS_CODE;
                        handler.sendMessage(msg);
                    }
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                // TODO Auto-generated method stub
                super.onFailure(error, content);
            }

        });
    }
    
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if(msg.what == Config.SUCCESSS_CODE){
                postNotify();
            }
        }
    };
    
    private void postNotify(){
        Notification nf = new Notification(R.drawable.ic_launcher, "亲！您今天还没有记日记噢。", System.currentTimeMillis());
        nf.defaults = Notification.DEFAULT_ALL;
        nf.flags = Notification.FLAG_AUTO_CANCEL;
        Intent i = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        nf.setLatestEventInfo(context, "关键字日记提醒", "亲！您今天还没有记日记噢。", pendingIntent);
        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, nf);
    }
}
