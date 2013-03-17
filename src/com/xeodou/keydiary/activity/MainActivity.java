package com.xeodou.keydiary.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
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
import android.os.Bundle;
import org.json.JSONObject;

import android.app.Activity;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        getFiveMonth();
        adapter = new DiaryFragementAdapter(diaries, titles);
        viewPager = (ViewPager)findViewById(R.id.diarycontent);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
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
        int m = Utils.getCurrentMonth();
        int y = Utils.getCurrentYear();
        m = m - 2;
        if(m < 0){
            m = 12 + m;
            y = y - 1;
        }
        for (int i = 0; i < 3; i++) {
            DiaryTime time = new DiaryTime();
            m++;
            if(m > 12){
                m = m - 12;
                y += 1;
            }
            time.setYear(y);
            time.setMonth(m);
            time.setDay(0);
            titles.add(time);
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
                Gson gson = new Gson();
                LoadDiary result = gson.fromJson(response.toString() , LoadDiary.class);
                if (result.getStat() == 1) {
                    List<Diary> ds = result.getData();
                    Map<String, Diary> hashMap = new HashMap<String, Diary>();
                    for(Diary d : ds){
                        hashMap.put(d.getD(), d);
                    }
                    diaries.putAll(hashMap);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
                // TODO Auto-generated method stub
                super.onFailure(e, errorResponse);
            }

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                super.onStart();
            }
            
        });
    } 
    
    private void addPager(int i, int p){
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
                addPager(-1, position);
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


}
