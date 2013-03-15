package com.xeodou.keydiary.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.actionbarsherlock.view.Menu;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.viewpagerindicator.TitlePageIndicator;
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
import org.holoeverywhere.app.Activity;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

public class MainActivity extends Activity {

    private final String TAG = "MainActivity";
    private TitlePageIndicator pageIndicator;
    private ViewPager viewPager;
    private DiaryFragementAdapter adapter;
    private Map<String, Diary> diaries;
    private List<DiaryTime> titles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!Utils.isLogin(this)){
//            login();
//            return;
        }
        addCurrentDiary(Config.ACTION_ADD_DIARY_CURRENT);
        init();
    }
    
    private void init(){
        diaries = new HashMap<String, Diary>();
        titles = new ArrayList<DiaryTime>();
        getFiveMonth();
        adapter = new DiaryFragementAdapter(diaries, titles);
        pageIndicator = (TitlePageIndicator)findViewById(R.id.diarytitles);
        viewPager = (ViewPager)findViewById(R.id.diarycontent);
        viewPager.setAdapter(adapter);
        pageIndicator.setViewPager(viewPager, 3);
        pageIndicator.setBackgroundColor(Color.BLACK);
        pageIndicator.setTextColor(getResources().getColor(R.color.title_indicator_text_color));
//        pageIndicator.setSelectedColor();
        pageIndicator.setOnPageChangeListener(pageChangeListener);
    }
    
    private void getFiveMonth(){
        int m = Utils.getCurrentMonth();
        int y = Utils.getCurrentYear();
        m = m - 2;
        if(m < 0){
            m = 12 + m;
            y = y - 1;
        }
        for (int i = 0; i < 5; i++) {
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
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setAction(Config.ACTION_LOGIN);
            startActivityForResult(intent, Config.LOGIN_CODE);
        }
        return;
    }
    
    private void laodAllDiaries(){
        API.getALLDiaries(new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                // TODO Auto-generated method stub
                Log.d(TAG, response.toString());
                Gson gson = new Gson();
                LoadDiary result = gson.fromJson(response.toString() , LoadDiary.class);
                if (result.getStat() == 1) {
                    List<Diary> diaries = result.getData();
                    Map<String, Diary> hashMap = new HashMap<String, Diary>();
                    for(Diary d : diaries){
                        hashMap.put(d.getD(), d);
                    }
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
    
    private void addCurrentDiary(String action){
        Intent intent = new Intent(this, AddDiaryActivity.class);
        intent.setAction(action);
        startActivity(intent);
    }
    
    private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {
        
        @Override
        public void onPageSelected(int position) {
            // TODO Auto-generated method stub
            
        }
        
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // TODO Auto-generated method stub
            
        }
        
        @Override
        public void onPageScrollStateChanged(int state) {
            // TODO Auto-generated method stub
            
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
//        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Config.FAIL_CODE){
            finish();
        } else if(resultCode == Config.LOGIN_CODE) {
            addCurrentDiary(Config.ACTION_ADD_DIARY_CURRENT);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        this.getMenuInflater().inflate(R.menu.activity_main, (android.view.Menu) menu);
        return true;
    }

}
