package com.xeodou.keydiary.views;

import java.util.Map;
import com.xeodou.keydiary.R;
import com.xeodou.keydiary.Utils;
import com.xeodou.keydiary.bean.Diary;

import android.app.Dialog;
import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class EditDialog extends Dialog {

    private Context context;
    private String date;
    private String content;
    private EditText editText;
    private Map<String, Diary> diaries;
    private TextView title;
    private onDialogClickListener dialogClickListener;
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_edit, null);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        this.addContentView(v, params);
        editText = (EditText)v.findViewById(R.id.dialog_edit);
        View delete = (View)v.findViewById(R.id.dialog_delete);
        View ok = (View)v.findViewById(R.id.dialog_ok);
        title = (TextView)v.findViewById(R.id.dialog_title);
        delete.setOnClickListener(clickListener);
        ok.setOnClickListener(clickListener);
        if(Utils.getTypeface() != null){
            editText.setTypeface(Utils.getTypeface());
        }
    }
    
    public void setDialogTitle(String text){
        if(title != null && text != null){
            title.setText(text);
        }
    }
    
    public void setEditContent(String content){
        if(editText != null && content != null){
            editText.setText(content);
            editText.setSelection(content.length());
        }
    }
    
    private android.view.View.OnClickListener clickListener = new android.view.View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
            case R.id.dialog_delete:
                if(dialogClickListener != null){
                    dialogClickListener.onClick(ClickType.Delete, editText.getText().toString(), title.getText().toString(), v);
                }
                break;

            case R.id.dialog_ok:
                if(dialogClickListener != null){
                    dialogClickListener.onClick(ClickType.Update, editText.getText().toString(), title.getText().toString(), v);
                }
                break;
            }
            cancel();
        }
    }; 
    
    public void setOnDialogClickListener(onDialogClickListener dialogClickListener){
        this.dialogClickListener = dialogClickListener;
    }
    
    public interface onDialogClickListener{
        void onClick(ClickType type, String content, String day, View v);
    }
    
    public enum ClickType{
        Delete("delete"),
        Update("update"),
        Add("add");
        String type;
        ClickType(String type){
            this.type = type;
        }
        
        public String getType(){
            return this.type;
        }
    }
}
