package com.xeodou.keydiary.views;

import java.util.Map;

import com.xeodou.keydiary.PanningEditText;
import com.xeodou.keydiary.R;
import com.xeodou.keydiary.bean.Diary;

import android.app.Dialog;
import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView.OnEditorActionListener;

public class EditDialog extends Dialog {

    private Context context;
    private String date;
    private String content;
    private PanningEditText editText;
    private Map<String, Diary> diaries;
    public EditDialog(Context context) {
        this(context, 0);
        // TODO Auto-generated constructor stub
    }
    
    public EditDialog(Context context, int theme) {
        super(context, theme);
        // TODO Auto-generated constructor stub
        this.context = context;
        initView();
    }
    
    public EditDialog(Context context, Map<String, Diary> diaries){
        this(context, 0);
        this.diaries = diaries;
    }
    
    public EditDialog(Context context, String date, String content){
        this(context, 0);
        this.date = date;
        this.content = content;
        initData();
    }
    
    private void initData(){
        if(editText != null && content != null){
            editText.setText(content);
        }
    }
    
    public void setContent(String str){
        if(editText != null){
            this.content = str;
            editText.setText(str);
        } else {
            try {
                throw new Exception("edittext should be init");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    public void setDate(String date){
        this.date = date;
    }
    
    public String getDate(){
        return this.date;
    }
    
    public void setOnEditorActionListener(OnEditorActionListener editorActionListener){
        if(editText != null){
            editText.setOnEditorActionListener(editorActionListener);
        } else {
            try {
                throw new Exception("edittext should be init");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void initView(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_edit, null);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        this.addContentView(v, params);
        editText = (PanningEditText)v.findViewById(R.id.dialog_edt);
    }
    
    private void sendMsg(int code, Object obj){
        Message msg = new Message();
        msg.what = code;
        msg.obj = obj;
//        handler.sendMessage(msg);
    }
    
}
