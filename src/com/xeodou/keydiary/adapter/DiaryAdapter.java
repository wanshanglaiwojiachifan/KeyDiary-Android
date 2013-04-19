package com.xeodou.keydiary.adapter;

import java.sql.SQLException;
import java.util.Map;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.xeodou.keydiary.Config;
import com.xeodou.keydiary.MyApplication;
import com.xeodou.keydiary.PanningEditText;
import com.xeodou.keydiary.R;
import com.xeodou.keydiary.Utils;
import com.xeodou.keydiary.bean.Diary;
import com.xeodou.keydiary.bean.LoadADiary;
import com.xeodou.keydiary.bean.Result;
import com.xeodou.keydiary.database.DBHelper;
import com.xeodou.keydiary.database.DBUtils;
import com.xeodou.keydiary.http.API;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Typeface;
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
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class DiaryAdapter extends BaseAdapter {

    class ViewHolder {
        TextView day;
        PanningEditText content;
        Button uploadBtn;
    }
    
    private Map<String, Diary> diaries;
    private Context context;
    private int year, month;
    private ProgressDialog dialog;
    private String text;
    private EditText editText;
    private Diary diaryData;
    public DiaryAdapter(Context context, Map<String, Diary> diaries,int year, int month){
        this.context = context;
        this.diaries = diaries;
        this.year = year;
        this.month = month;
        this.diaryData = null;
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
    
    public String getDay(int position){
        return year + "-" + month + "-" + ( position + 1);
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
            viewHolder.uploadBtn = (Button)convertView.findViewById(R.id.updload_btn);
            editText = (PanningEditText)convertView.findViewById(R.id.content_diary_tv);
            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder)convertView.getTag();
//        }
        if( year > Utils.getCurrentYear()){
            editText.setEnabled(false);
        } else if(year == Utils.getCurrentYear()){
            if(month > Utils.getCurrentMonth()){
                editText.setEnabled(false);
            } else if(month == Utils.getCurrentMonth()){
                if(position + 1 > Utils.getCurrentDay()){
                    editText.setEnabled(false);
                }
            }
        }
        final String day = year+"-"+Utils.douInt(month)+"-" + Utils.douInt(position + 1);
        viewHolder.day.setText(Utils.douInt(position+1));
        Diary diary = diaries.get(year+"-"+Utils.douInt(month)+"-" + Utils.douInt(position + 1));
        boolean isLocal = false;
        if(diary != null && diary.getIsLocal() != null) isLocal = diary.getIsLocal();
        if(isLocal){
            viewHolder.uploadBtn.setVisibility(View.VISIBLE);
            viewHolder.uploadBtn.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("未同步日记");
                    builder.setMessage("由于网络不好日记没有保存成功\n是否重新发送！");
                    builder.setNegativeButton("算了", new DialogInterface.OnClickListener() {
                        
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            deleeteLocal(day);
                            dialog.cancel();
                        }
                    });
                    builder.setPositiveButton("重新发送", new DialogInterface.OnClickListener() {
                        
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            upsertDiary(context, day, diaries.get(day).getContent());
                        }
                    });
                    builder.show();
                }
            });
        }
        
        editText.setOnKeyListener(new OnKeyListener() {
            
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {  
                    String str = ((EditText)v).getText().toString();
                    Diary diary = null;
                    if(diaries.containsKey(day)) {
                        if(str != null) {
                            if(str.equals("")){
                               delDiary(context, day);
                            } else {
                                diary = diaries.get(day);
                                if(str.equals(diary.getContent())){
                                    ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                                            editText.getWindowToken(), 0);
                                    return false;  
                                } 
                                diary.setContent(str);
                                diaryData = diary;
                                updateDiary(context, day, str, diary);
                            }
                        }
                    } else {
                        if(str != null && str.length() > 0){
                            diary = new Diary();
                            diary.setD(day);
                            diary.setContent(str);
                            diary.setDid((int)Math.random() * 1000 + "");
                            diaryData = diary;
                            addDiary(context, day, str);
                        }
                    }
                  ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                  editText.getWindowToken(), 0);
                    return false;  
                }
                return false;
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
                                diaryData = diary;
                                updateDiary(context, day, str, diary);
                            }
                        }
                    } else {
                        if(str != null && str.length() > 0){
                            diary = new Diary();
                            diary.setD(day);
                            diary.setContent(str);
                            diary.setDid((int)Math.random() * 1000 + "");
                            diaryData = diary;
                            addDiary(context, day, str);
                        }
                    }
                }
                v.clearFocus();
                v.setSelected(false);
//                ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
//                        editText.getWindowToken(), 0);
                return false;
            }
        });
        if(diary != null){
            editText.setText(diary.getContent());
        }
        
        if(Utils.getTypeface() != null){
            editText.setTypeface(Utils.getTypeface());
            viewHolder.day.setTypeface(Utils.getTypeface());
        }
        
        editText.getRootView().getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            
            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                if (context.getResources().getConfiguration().keyboardHidden == Configuration.KEYBOARDHIDDEN_YES) { // Check if keyboard is not hidden
                    // ... do something here
                    Crouton.showText((Activity)context, "key board hidden！", Style.ALERT);
                    editText.clearFocus();
                 }
            }
        });
        
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
                    sendMsg(Config.SUCCESSS_CODE, "添加日记成功");
                } else {
                    sendMsg(Config.FAIL_ADD, "添加日记失败");
                }            
            }

            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
                // TODO Auto-generated method stub
                Gson gson = new Gson();
                try {
                    Result result = gson.fromJson(errorResponse.toString(), Result.class);
                    if(result.getStat() == 2101){
                        sendMsg(Config.FAIL_TO_LONG, "您的日记太长");
                    } else {
                        sendMsg(Config.FAIL_ADD, "添加日记失败");
                    }
                } catch (JsonSyntaxException e1) {
                    // TODO Auto-generated catch block
                    sendMsg(Config.FAIL_ADD, "添加日记失败");
                }
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
                Gson gson = new Gson();
                try {
                    Result result = gson.fromJson(errorResponse.toString(), Result.class);
                    if(result.getStat() == 2101){
                        sendMsg(Config.FAIL_TO_LONG, "您的日记太长");
                    } else {
                        sendMsg(Config.FAIL_UPDATE, "修改日记失败");
                    }
                } catch (JsonSyntaxException e1) {
                    // TODO Auto-generated catch block
                    sendMsg(Config.FAIL_UPDATE, "修改日记失败");
                }
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
                    sendMsg(Config.FAIL_UPDATE, "修改日记失败");
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
    
    private void upsertDiary(final Context c ,String date, String content){
        API.upsertDiary(date, content, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                // TODO Auto-generated method stub
                Gson gson = new Gson();
                LoadADiary result = gson.fromJson(response.toString(), LoadADiary.class);
                if(result.getStat() == 1 && result.getData() != null ) {
                    diaryData = diaries.get(result.getData().getD());
                    diaryData.setIsLocal(false);
                    diaries.put(diaryData.getD(), diaryData);
                    sendMsg(Config.SUCCESS_UPSERT, "上传日记成功");
                } else {
                    sendMsg(Config.FAIL_UPSERT, "上传日记失败");
                }  
            }

            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
                // TODO Auto-generated method stub
                sendMsg(Config.FAIL_UPSERT, "上传日记失败");
            }

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                if(dialog == null){
                    dialog = ProgressDialog.show(c, null, "正在上传日记...");
                    dialog.setCancelable(true);
                }
            }
            
        });
    }
    
    
    private void deleeteLocal(String day){
        try {
            Dao<Diary , Integer> diaryDao = DBUtils.getHelper(context).getDiaryDao();
            Diary d = diaries.get(day);
            if(d !=null){
                diaryDao.delete(d);
                diaries.remove(d.getD());
                Crouton.showText((Activity)context, "数据已清除 ！", Style.INFO);
                notifyDataSetChanged();
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            Crouton.showText((Activity)context, "数据库错误 ！", Style.ALERT);
        }
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
            if(dialog !=null && dialog.isShowing()) dialog.dismiss();
            dialog = null;
            if(msg.what == Config.SUCCESSS_CODE){
                if(diaryData != null) diaryData = null;
                if(str == null) str = "修改失败";
                Crouton.showText((Activity)context, str, Style.INFO);
                return;
            }
            if(msg.what == Config.SUCCESS_UPSERT){
                if(msg.obj != null) Crouton.showText((Activity)context, msg.obj.toString(), Style.INFO);
                try {
                    Dao<Diary , Integer> diaryDao = DBUtils.getHelper(context).getDiaryDao();
                    diaryDao.delete(diaryData);
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    Crouton.showText((Activity)context, "数据库错误 ！", Style.ALERT);
                }
                return;
            }
            if(str == null) str = "修改失败";
            if(str.length() <= 0) str = "修改失败";
            Crouton.showText((Activity)context, str, Style.ALERT);
            if(diaryData != null  && msg.what != Config.FAIL_TO_LONG){
                diaryData.setIsLocal(true);
                diaryData.setCreated(Utils.getFormatDate());
                try {
                    Dao<Diary , Integer> diaryDao = DBUtils.getHelper(context).getDiaryDao();
                    if(msg.what == Config.FAIL_ADD || msg.what == Config.FAIL_UPDATE || msg.what == Config.FAIL_UPSERT){
                        diaryDao.createOrUpdate(diaryData);
                        diaries.put(diaryData.getD(), diaryData);                        
                        Crouton.showText((Activity)context, "您的日记已经被缓存到本地", Style.INFO);
                        notifyDataSetChanged();
                    }                 
//                    diaryDao.update(diaryData);
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    Crouton.showText((Activity)context, "插入数据库失败！", Style.ALERT);
                }
            }
        }
    };
}
