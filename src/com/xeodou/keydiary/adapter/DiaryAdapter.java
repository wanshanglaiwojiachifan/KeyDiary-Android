package com.xeodou.keydiary.adapter;

import java.util.Map;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.xeodou.keydiary.Config;
import com.xeodou.keydiary.R;
import com.xeodou.keydiary.Utils;
import com.xeodou.keydiary.bean.Diary;
import com.xeodou.keydiary.bean.LoadADiary;
import com.xeodou.keydiary.bean.Result;
import com.xeodou.keydiary.http.API;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class DiaryAdapter extends BaseAdapter {

    class ViewHolder {
        TextView day;
        EditText content;
    }
    
    private Map<String, Diary> diaries;
    private Context context;
    private int year, month;
    private ProgressDialog dialog;
    private String text;
    public DiaryAdapter(Context context, Map<String, Diary> diaries,int year, int month){
        this.context = context;
        this.diaries = diaries;
        this.year = year;
        this.month = month;
    }
    
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return Utils.getDayOfMonth(year, month);
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return diaries.get(year+"-"+Utils.douInt(month)+"-" + Utils.douInt(position));
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder = null;
//        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.diary_item_layout, null);
            viewHolder.day = (TextView)convertView.findViewById(R.id.day_diary_tv);
            viewHolder.content = (EditText)convertView.findViewById(R.id.content_diary_tv);
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder)convertView.getTag();
//        }
        if( year > Utils.getCurrentYear()){
            viewHolder.content.setEnabled(false);
        } else {
            if(month > Utils.getCurrentMonth()){
                viewHolder.content.setEnabled(false);
            } else {
                if(position + 1 > Utils.getCurrentDay()){
                    viewHolder.content.setEnabled(false);
                }
            }
        }
        final String day = year+"-"+Utils.douInt(month)+"-" + Utils.douInt(position + 1);
        viewHolder.day.setText(Utils.douInt(position+1));
        viewHolder.content.setOnTouchListener(new OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                text = ((EditText)v).getText().toString();
                return false;
            }
        });
        viewHolder.content.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                // TODO Auto-generated method stub
                text = s.toString();
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                
            }
        });
        viewHolder.content.setOnEditorActionListener(new OnEditorActionListener() {
            
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // TODO Auto-generated method stub
                if(v.isSelected()) v.setSelected(false);
                if(v.isFocused()) v.setSelected(false);
                if(v.isInEditMode()) v.setFocusable(false);
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    String str = v.getText().toString();
//                    if(text == null || text.length() <= 0){
//                        if(str != null && str.length() > 0){
//                            addDiary(context, day, str);
//                        }
//                    } else {
//                        if(str != null && str.equals(text)){
//                            Diary diary = diaries.get(day);
//                            diary.setContent(str);
//                            updateDiary(context, day, str, diary);
//                        }
//                    }
                    if(str != null && str.length() > 0){
                      Diary diary = null;
                      if(diaries.containsKey(day))
                          diary = diaries.get(day);
                      if(diary == null){ 
//                          diary = new Diary();
//                          diary.setD(day);
//                          diary.setDid((int)(month * Math.random()) + (int)(year * Math.random()) + "");
//                          diary.setContent(str);
//                          
                          addDiary(context, day, str);
                          return false;
                      }
                      diary.setContent(str);
                      updateDiary(context, day, str, diary);   
                    }
                }
                return false;
            }
        });
        Diary diary = diaries.get(year+"-"+Utils.douInt(month)+"-" + Utils.douInt(position + 1));
        if(diary != null){
            viewHolder.content.setText(diary.getContent());
        }
        return convertView;
    }

    public void addDiary(final Context c,String data, String content){
        API.addDiary(data, content, new JsonHttpResponseHandler(){
            
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                // TODO Auto-generated method stub
                Gson gson = new Gson();
                LoadADiary result = (LoadADiary)gson.fromJson(response.toString(), LoadADiary.class);
                if(result.getStat() == 1 && result.getData() != null ) {
                    diaries.put(result.getData().getD(), result.getData());
                    sendMsg(Config.SUCCESSS_CODE, null);
                } else {
                    sendMsg(Config.FAIL_CODE, "添加日记失败");
                }            
            }

            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
                // TODO Auto-generated method stub
                sendMsg(Config.FAIL_CODE, "添加日记失败");
            }
            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                if(dialog == null){
                    dialog = ProgressDialog.show(c, null, "正在添加日记...");
                    dialog.setCancelable(true);
                }
            }
            
        });
    } 
    
    public void updateDiary(final Context c,final String data, String content, final Diary diary){
        API.updateDiary(data, content, new JsonHttpResponseHandler(){

            

            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
                // TODO Auto-generated method stub
                sendMsg(Config.FAIL_CODE, "修改日记失败");
            }

            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                // TODO Auto-generated method stub
                Gson gson = new Gson();
                Result result = (Result)gson.fromJson(response.toString(), Result.class);
                if(result.getStat() == 1) {
                    diaries.put(data, diary);
                    sendMsg(Config.SUCCESSS_CODE, null);
                } else {
                    sendMsg(Config.FAIL_CODE, "修改日记失败");
                }
            }

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                if(dialog == null){
                    dialog = ProgressDialog.show(c, null, "正在修改日记...");
                    dialog.setCancelable(true);
                }
            }
            
        });
    } 
    
    private void sendMsg(int code, Object obj){
        Message msg = new Message();
        msg.what = code;
        msg.obj = obj;
        handler.sendMessage(msg);
    }
    
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if(dialog.isShowing()) dialog.dismiss();
            dialog = null;
            if(msg.what == Config.SUCCESSS_CODE){
                Crouton.showText((Activity)context, "修改成功", Style.INFO);
                return;
            }
            String str = msg.obj.toString();
            if(str == null) str = "修改失败";
            if(str.length() <= 0) str = "修改失败";
            Crouton.showText((Activity)context, str, Style.ALERT);
        }
        
    };
}
