package com.xeodou.keydiary.adapter;

import java.util.Map;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.xeodou.keydiary.Config;
import com.xeodou.keydiary.MyApplication;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
    private EditText editText;
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
            editText = (EditText)convertView.findViewById(R.id.content_diary_tv);
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder)convertView.getTag();
//        }
        if( year > Utils.getCurrentYear()){
            editText.setEnabled(false);
        } else {
            if(month > Utils.getCurrentMonth()){
                editText.setEnabled(false);
            } else {
                if(position + 1 > Utils.getCurrentDay()){
                    editText.setEnabled(false);
                }
            }
        }
        final String day = year+"-"+Utils.douInt(month)+"-" + Utils.douInt(position + 1);
        viewHolder.day.setText(Utils.douInt(position+1));
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if(!hasFocus)
                    editText.setBackgroundResource(R.drawable.edit_text_n);
            }
        });
        editText.setOnTouchListener(new OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                editText.setBackgroundResource(R.drawable.edit_text_n);
                return false;
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if(editText.isSelected() && editText.isFocused()){
                    if (calculateLength(s) > 7) {
                        editText.setBackgroundResource(R.drawable.edit_text_e);
                    } else {
                        editText.setBackgroundResource(R.drawable.edit_text_s);
                    }
                }
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                // TODO Auto-generated method stub
//                editText.setBackgroundResource(R.drawable.edit_text_s);
                text = s.toString();
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                
            }
        });
        editText.setOnEditorActionListener(new OnEditorActionListener() {
            
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // TODO Auto-generated method stub
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    String str = v.getText().toString();
                    Diary diary = null;
                    if(diaries.containsKey(day)) {
                        if(str != null) {
                            if(str.equals("")){
                               delDiary(context, day);
                            } else {
                                diary = diaries.get(day);
                                if(str.equals(diary.getContent())) return false;
                                diary.setContent(str);
                                updateDiary(context, day, str, diary);
                            }
                        }
                    } else {
                        if(str != null && str.length() > 0){
                            addDiary(context, day, str);
                        }
                    }
                }
                v.clearFocus();
                v.setSelected(false);
                ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                        editText.getWindowToken(), 0);
                return false;
            }
        });
        Diary diary = diaries.get(year+"-"+Utils.douInt(month)+"-" + Utils.douInt(position + 1));
        if(diary != null){
            editText.setText(diary.getContent());
        }
        return convertView;
    }

    private long calculateLength(CharSequence c) {  
        double len = 0;  
        for (int i = 0; i < c.length(); i++) {  
            int tmp = (int) c.charAt(i);  
            if (tmp > 0 && tmp < 127) {  
                len += 0.5;  
            } else {  
                len++;  
            }  
        }  
        return Math.round(len);  
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
                    sendMsg(Config.SUCCESSS_CODE, "添加日记成功");
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
    
    public void delDiary(final Context c,final String data){
        API.deleteDiary(data, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
                // TODO Auto-generated method stub
                sendMsg(Config.FAIL_CODE, "删除日记失败");
            }

            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                // TODO Auto-generated method stub
                Gson gson = new Gson();
                Result result = (Result) gson.fromJson(response.toString(),
                        Result.class);
                if (result.getStat() == 1) {
                    diaries.remove(data);
                    sendMsg(Config.SUCCESSS_CODE, "删除日记成功");
                } else {
                    sendMsg(Config.FAIL_CODE, "删除日记失败");
                }
            }

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                if (dialog == null) {
                    dialog = ProgressDialog.show(c, null, "正在修改日记...");
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
                    sendMsg(Config.SUCCESSS_CODE, "修改日记成功");
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
            String str = msg.obj.toString();
            if(dialog.isShowing()) dialog.dismiss();
            dialog = null;
            if(msg.what == Config.SUCCESSS_CODE){
                if(str == null) str = "修改失败";
                Crouton.showText((Activity)context, str, Style.INFO);
                return;
            }
            if(str == null) str = "修改失败";
            if(str.length() <= 0) str = "修改失败";
            Crouton.showText((Activity)context, str, Style.ALERT);
        }
        
    };
}
