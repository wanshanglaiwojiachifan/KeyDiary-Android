package com.xeodou.keydiary.adapter;

import java.sql.SQLException;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.j256.ormlite.dao.Dao;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.xeodou.keydiary.Config;
import com.xeodou.keydiary.KeyDiaryResult;
import com.xeodou.keydiary.PanningEditText;
import com.xeodou.keydiary.PanningEditText.onLostFocusListener;
import com.xeodou.keydiary.R;
import com.xeodou.keydiary.Utils;
import com.xeodou.keydiary.bean.Diary;
import com.xeodou.keydiary.bean.LoadADiary;
import com.xeodou.keydiary.bean.Result;
import com.xeodou.keydiary.database.DBUtils;
import com.xeodou.keydiary.http.API;
import com.xeodou.keydiary.views.EditDialog;
import com.xeodou.keydiary.views.EditDialog.ClickType;
import com.xeodou.keydiary.views.EditDialog.onDialogClickListener;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class DiaryAdapter extends BaseAdapter {

    class ViewHolder {
        TextView day;
        TextView content;
        Button uploadBtn;
    }
    
    private Map<String, Diary> diaries;
    private Context context;
    private int year, month;
    private ProgressDialog dialog;
    private Diary diaryData;
    private boolean lock = false;
    private ListView listView;
    public DiaryAdapter(Context context,ListView listView, Map<String, Diary> diaries,int year, int month){
        this.context = context;
        this.diaries = diaries;
        this.year = year;
        this.month = month;
        this.diaryData = null;
        this.listView = listView;
        listView.setOnItemClickListener(itemClickListener);
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
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.diary_item_layout, null);
            viewHolder.day = (TextView)convertView.findViewById(R.id.day_diary_tv);
            viewHolder.uploadBtn = (Button)convertView.findViewById(R.id.updload_btn);
            viewHolder.content = (TextView)convertView.findViewById(R.id.content_diary_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
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
        
       
        if(diary != null){
            viewHolder.content.setText(diary.getContent());
        } else {
            viewHolder.content.setText("");
        }
        
        return convertView;
    }
    
    private OnItemClickListener itemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View v, final int position,
                long id) {
            // TODO Auto-generated method stub
            Diary diary = diaries.get(year+"-"+Utils.douInt(month)+"-" + Utils.douInt(position));
            EditDialog editDialog = new EditDialog(context);
            editDialog.setDialogTitle(year + "-" + Utils.douInt(month) + "-" + Utils.douInt(position));
            if(diary != null){
                editDialog.setEditContent(diary.getContent());
            } else {
                editDialog.setEditContent("");
            }
            editDialog.setOnDialogClickListener(new onDialogClickListener() {
                
                @Override
                public void onClick(ClickType type, String content, String day, View v) {
                    // TODO Auto-generated method stub
                    if(type.equals(ClickType.Delete)){
                        Diary diary = new Diary();
                        diary.setD(day);
                        diaryData = diary;
                        delDiary(context, day);
                    } else {
                        action(content, day);
                    }
                }
            });
            editDialog.show();
        }
    };
    
    private boolean action(String str, String day) {
        Diary diary = null;
        if (diaries.containsKey(day)) {
            diary = diaries.get(day);
            if (str.equals(diary.getContent())) {
                return false;
            }
            diary.setContent(str);
            diaryData = diary;
            updateDiary(context, day, str, diary);
        } else {
            if (str != null && str.length() > 0) {
                diary = new Diary();
                diary.setD(day);
                diary.setContent(str);
                diary.setDid((int) Math.random() * 1000 + "");
                diaryData = diary;
                addDiary(context, day, str);
            }
        }
        return false;
    }

    public void addDiary(final Context c,String data, String content){
        API.addDiary(data, content, new AsyncHttpResponseHandler(){
            
            @Override
            public void onSuccess(int statusCode, String content) {
                // TODO Auto-generated method stub
                Gson gson = new Gson();
                LoadADiary result = (LoadADiary)gson.fromJson(content, LoadADiary.class);
                if(result.getStat() == 1 && result.getData() != null ) {
                    diaries.put(result.getData().getD(), result.getData());
                    sendMsg(Config.SUCCESSS_CODE, "添加日记成功");
                } else {
                    sendMsg(Config.FAIL_ADD, "添加日记失败");
                }            
            }

            @Override
            public void onFailure(Throwable error, String content) {
                // TODO Auto-generated method stub
                Gson gson = new Gson();
                try {
                    Result result = gson.fromJson(content, Result.class);
                    if(result.getStat() == 2101){
                        sendMsg(Config.FAIL_TO_LONG, "您的日记太长");
                    } else {
                        sendMsg(Config.FAIL_ADD, KeyDiaryResult.getMsg(result.getStat()));
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
        API.deleteDiary(data, new AsyncHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, String content) {
                // TODO Auto-generated method stub
                Gson gson = new Gson();
                Result result = (Result) gson.fromJson(content,
                        Result.class);
                if (result.getStat() == 1) {
                    diaries.remove(data);
                    sendMsg(Config.SUCCESS_DELETE, "删除日记成功");
                } else {
                    sendMsg(Config.FAIL_CODE, "删除日记失败");
                }
           }

            @Override
            public void onFailure(Throwable error, String content) {
                // TODO Auto-generated method stub
                sendMsg(Config.FAIL_CODE, "删除日记失败");
            }

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                if (dialog == null) {
                    dialog = ProgressDialog.show(c, null, "正在删除日记...");
                    dialog.setCancelable(true);
                }
            }

        });
    } 
    
    public void updateDiary(final Context c,final String data, String content, final Diary diary){
        API.updateDiary(data, content, new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, String content) {
                // TODO Auto-generated method stub
                Gson gson = new Gson();
                Result result = (Result)gson.fromJson(content, Result.class);
                if(result.getStat() == 1) {
                    diaries.put(data, diary);
                    sendMsg(Config.SUCCESSS_CODE, "修改日记成功");
                } else {
                    sendMsg(Config.FAIL_UPDATE, "修改日记失败");
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                // TODO Auto-generated method stub
                Gson gson = new Gson();
                try {
                    Result result = gson.fromJson(content, Result.class);
                    if(result.getStat() == 2101){
                        sendMsg(Config.FAIL_TO_LONG, "您的日记太长");
                    } else {
                        sendMsg(Config.FAIL_ADD, KeyDiaryResult.getMsg(result.getStat()));
                    }
                } catch (JsonSyntaxException e1) {
                    // TODO Auto-generated catch block
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
        API.upsertDiary(date, content, new AsyncHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, String content) {
                // TODO Auto-generated method stub
                Gson gson = new Gson();
                LoadADiary result = gson.fromJson(content, LoadADiary.class);
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
            public void onFailure(Throwable e, String content) {
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
            lock = false;
            if(dialog !=null && dialog.isShowing()) dialog.dismiss();
            dialog = null;
            switch (msg.what) {
            case Config.SUCCESSS_CODE:
                diaries.put(diaryData.getD(), diaryData);
                notifyDataSetChanged();
                if(diaryData != null) diaryData = null;
                if(str == null) str = "修改失败";
                Crouton.showText((Activity)context, str, Style.INFO);
                return;

            case Config.SUCCESS_UPSERT:
                if(msg.obj != null) Crouton.showText((Activity)context, msg.obj.toString(), Style.INFO);
                try {
                    Dao<Diary , Integer> diaryDao = DBUtils.getHelper(context).getDiaryDao();
                    diaryDao.delete(diaryData);
                    notifyDataSetChanged();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    Crouton.showText((Activity)context, "数据库错误 ！", Style.ALERT);
                }
                return;
            case Config.SUCCESS_DELETE:
                notifyDataSetChanged();
                diaryData = null;
                if(str == null) str = "删除失败";
                Crouton.showText((Activity)context, str, Style.INFO);
                return;            }
            
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
