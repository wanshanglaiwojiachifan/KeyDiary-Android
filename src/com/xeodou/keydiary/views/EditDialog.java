package com.xeodou.keydiary.views;

import java.util.Map;
import com.xeodou.keydiary.R;
import com.xeodou.keydiary.Utils;
import com.xeodou.keydiary.bean.Diary;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class EditDialog extends Dialog {

    private Context context;
    private String date;
    private String content;
    private EditText editText;
    private TextView title;
    private onDialogClickListener dialogClickListener;
    private String day;
    private ProgressBar bar;
    private View contentView;
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
    
    public void stopProgress(){
        bar.setVisibility(View.GONE);
    }
    
    public void startProgress(){
        bar.setVisibility(View.VISIBLE);
    }
    
    private void initView(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        LayoutInflater inflater = LayoutInflater.from(context);
        contentView = inflater.inflate(R.layout.dialog_edit, null);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        this.addContentView(contentView, params);
        editText = (EditText)contentView.findViewById(R.id.dialog_edit);
        View delete = (View)contentView.findViewById(R.id.dialog_delete);
        View ok = (View)contentView.findViewById(R.id.dialog_ok);
        title = (TextView)contentView.findViewById(R.id.dialog_title);
        bar = (ProgressBar)contentView.findViewById(R.id.dialog_pro);
        bar.setVisibility(View.GONE);
        delete.setOnClickListener(clickListener);
        ok.setOnClickListener(clickListener);
        setCanceledOnTouchOutside(true);
        editText.requestFocus();
        if(Utils.getTypeface() != null){
            editText.setTypeface(Utils.getTypeface());
        }
    }
    
    public void setDialogTitle(String text){
        if(title != null && text != null){
            day = text;
            text = text + " " + Utils.getDayOfWeek(text) ;
            title.setText(text);
        }
    }
    
    public void setEditContent(String content){
        if(editText != null && content != null){
            editText.setText(content);
            editText.setSelection(content.length());
            if(Utils.getTypeface() != null) {
                editText.setTypeface(Utils.getTypeface());
            }
        }
    }
    
    private android.view.View.OnClickListener clickListener = new android.view.View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
            case R.id.dialog_delete:
                cancel();
                if(dialogClickListener != null){
                    dialogClickListener.onClick(ClickType.Delete, editText.getText().toString(), day, v);
                }
                break;

            case R.id.dialog_ok:
                if(dialogClickListener != null){
                    dialogClickListener.onClick(ClickType.Update, editText.getText().toString(), day, v);
                }
                break;
            }
        }
    }; 
    
    
    
    
    @Override
    public void cancel() {
        // TODO Auto-generated method stub
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(),0);
        super.cancel();
    }

    @Override
    public void dismiss() {
        // TODO Auto-generated method stub
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(),0);
        super.dismiss();
    }

    @Override
    public void show() {
        // TODO Auto-generated method stub
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        super.show();
    }

    public void setOnDialogClickListener(onDialogClickListener dialogClickListener){
        this.dialogClickListener = dialogClickListener;
    }
    
    public interface onDialogClickListener{
        void onClick(ClickType type, String content, String day, View v);
    }
    
    public enum ClickType{
        Delete("delete"),
        Update("update"),
        Add("add"),
        Ok("ok"),
        Cancel("cancel");
        String type;
        ClickType(String type){
            this.type = type;
        }
        
        public String getType(){
            return this.type;
        }
    }
}
