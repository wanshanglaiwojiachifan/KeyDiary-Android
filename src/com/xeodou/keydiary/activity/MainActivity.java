package com.xeodou.keydiary.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.xeodou.keydiary.Config;
import com.xeodou.keydiary.Log;
import com.xeodou.keydiary.R;
import com.xeodou.keydiary.Utils;
import com.xeodou.keydiary.adapter.DiaryFragementAdapter;
import com.xeodou.keydiary.bean.Diary;
import com.xeodou.keydiary.bean.DiaryTime;
import com.xeodou.keydiary.bean.LoadDiary;
import com.xeodou.keydiary.bean.User;
import com.xeodou.keydiary.http.API;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

    private final String TAG = "MainActivity";
    private ViewPager viewPager;
    private DiaryFragementAdapter adapter;
    private Map<String, Diary> diaries;
    private List<DiaryTime> titles;
    private Button setBtn;
    private ProgressDialog dialog;
    private long startDate, endDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UmengUpdateAgent.update(this);
        if(!Utils.isLogin(this)){
            login();
            return;
        }
        init();
    }
    
    private void init(){
        setBtn = (Button)findViewById(R.id.set_btn);
        setBtn.setOnClickListener(clickListener);
        diaries = new HashMap<String, Diary>();
        titles = new ArrayList<DiaryTime>();
        adapter = new DiaryFragementAdapter(diaries, titles);
        viewPager = (ViewPager)findViewById(R.id.diarycontent);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(2);
        viewPager.setOnPageChangeListener(pageChangeListener);
        loadAllDiaries();
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        // Checks whether a hardware keyboard is available
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            Toast.makeText(this, "keyboard visible", Toast.LENGTH_SHORT).show();
        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            Toast.makeText(this, "keyboard hidden", Toast.LENGTH_SHORT).show();
        }
      super.onConfigurationChanged(newConfig);

    }

    private OnClickListener clickListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent intent = new Intent(MainActivity.this, SetActivity.class);
            intent.setAction(Config.ACTION_SET);
            startActivityForResult(intent, Config.REQ_CODE);
        }
    };
    
    private void getFiveMonth(){
        if(startDate <= 0) return;
        Date date = Utils.getDate(startDate);
        int m = date.getMonth() + 1;
        int y = date.getYear() + 1900;
        for (int i = 0; i < 5; i++) {
            DiaryTime time = new DiaryTime();
            time.setYear(y);
            time.setMonth(m);
            time.setDay(0);
            titles.add(time);
            m++;
            if(m > 12){
                m = m - 12;
                y += 1;
            }
        }
    }
    
    private void login(){
        if(!Utils.isLogin(this)){
            Intent intent = new Intent(this, SelectActivity.class);
            intent.setAction(Config.ACTION_LOGIN);
            startActivityForResult(intent, Config.LOGIN_CODE);
        }
        return;
    }
    
    private void loadAllDiaries(){
        API.getALLDiaries(new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                // TODO Auto-generated method stub
                Log.d(TAG, response.toString());
                Gson gson = null;
                try {
                    gson = new Gson();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    sendMsg(Config.FAIL_CODE, "解析数据失败，请重试！");
                }
                LoadDiary result = gson.fromJson(response.toString() , LoadDiary.class);
                if (result.getStat() == 1 && result.getData() != null) {
                    List<Diary> ds = result.getData().getDiaries();
                    startDate = result.getData().getStartDate();
                    endDate = result.getData().getEndDate();
                    if(ds == null || ds.size() <= 0){
                        sendMsg(Config.FAIL_CODE, "你还没有添加任何日记");
                        return;
                    }
                    Map<String, Diary> hashMap = new HashMap<String, Diary>();
                    for(Diary d : ds){
                        hashMap.put(d.getD(), d);
                    }
                    diaries.putAll(hashMap);
                    sendMsg(Config.SUCCESSS_CODE, null);
                } else {
                    sendMsg(Config.FAIL_CODE, null);
                }
            }

            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
                // TODO Auto-generated method stub
                sendMsg(Config.FAIL_CODE, errorResponse.toString());
            }

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                if (dialog == null) {
                    dialog = ProgressDialog.show(MainActivity.this, null, "正在加载所有日记");
                }
            }
            
        });
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
            if(dialog != null && dialog.isShowing() ) dialog.dismiss();
            dialog = null;
            if(msg.what == Config.SUCCESSS_CODE){
                getFiveMonth();
                adapter.notifyDataSetChanged();
                Crouton.showText(MainActivity.this , "加载成功", Style.INFO);
                return ;
            }
            String str = msg.obj.toString();
            if(str == null) str = "加载失败";
            if(str.length() <= 0) str = "加载失败";
            Crouton.showText(MainActivity.this, str, Style.ALERT);
        }
        
    };
    
    private void addPager(int i, int p){
        Date date = Utils.getDate(endDate);
        DiaryTime time = titles.get(p);
        int y = time.getYear();
        int m = time.getMonth();
        time = null;
        time = new DiaryTime();
        time.setDay(0);
        if(i == 1){
            m += 2;
            if(m > 12) { 
                m = m -12;
                y ++;
            }
            if(y == date.getYear() + 1900 && m == date.getMonth() + 1) return;
            time.setYear(y);
            time.setMonth(m);
            titles.add(time);
        } else if(i == -1){
            m --;
            if(m <= 0){
                m += 12;
                y --;
            }
            time.setYear(y);
            time.setMonth(m);
            List<DiaryTime> arr = new ArrayList<DiaryTime>();
            arr.add(time);
            arr.addAll(titles);
            titles.clear();
            titles.addAll(arr);
            arr = null;
            
        }
        adapter.notifyDataSetChanged();
    }
    
    private int last;
    private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {
        
        @Override
        public void onPageSelected(int position) {
            // TODO Auto-generated method stub
//            Toast.makeText(MainActivity.this, position + " position", Toast.LENGTH_SHORT).show();
            if((last > position) && position - 2 <= 0 ){
//                Toast.makeText(MainActivity.this, last - position +  "left", Toast.LENGTH_SHORT).show();
//                addPager(-1, position);
            } else if((last < position) && position + 2 == titles.size()) {
//                Toast.makeText(MainActivity.this, last - position + "right", Toast.LENGTH_SHORT).show();
                addPager(1, position);
            }
            last = position;
        }
        
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // TODO Auto-generated method stub
//            Toast.makeText(MainActivity.this, position + "--" + positionOffset , Toast.LENGTH_SHORT).show();
        }
        
        @Override
        public void onPageScrollStateChanged(int state) {
            // TODO Auto-generated method stub
           if(state == ViewPager.SCROLL_STATE_SETTLING){
//               adapter.notifyDataSetChanged();
           }     
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
//        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Config.FAIL_CODE){
            finish();
        } else if(resultCode == Config.LOGIN_CODE) {
            init();
        } else if(resultCode == Config.LOGOUT_CODE){
            finish();
        }
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
