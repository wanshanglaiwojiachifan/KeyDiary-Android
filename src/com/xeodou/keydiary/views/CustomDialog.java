package com.xeodou.keydiary.views;

import com.xeodou.keydiary.R;
import com.xeodou.keydiary.Utils;
import com.xeodou.keydiary.views.EditDialog.ClickType;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

public class CustomDialog extends Dialog {

    private Context context;
    private TextView info, title;
    private onDialogInfoConfirmListener confirmListener;
    private Button left , right;
    public CustomDialog(Context context) {
        this(context, null);
        // TODO Auto-generated constructor stub
    }
    
    public CustomDialog(Context context, onDialogInfoConfirmListener confirmListener) {
        super(context);
        // TODO Auto-generated constructor stub
        this.confirmListener = confirmListener;
        this.context = context;
        initView();
    }
    

    public void setDialogInfo(String title){
        if(info != null){
            info.setText(title);
            if(Utils.getTypeface() != null) {
                info.setTypeface(Utils.getTypeface());
            }
        }
    }
    
    public void setDialogTitle(String str){
        if(title != null){
            title.setText(str);
        }
    }
    
    public void setLeftBtnText(String str){
        if(left != null) left.setText(str);
    }
    
    public void setRightBtnText(String str){
        if(right != null) right.setText(str);
    }
    
    private void initView(){
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_info, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        info = (TextView)v.findViewById(R.id.dialog_content);
        title =(TextView)v.findViewById(R.id.dialog_title); 
        left = (Button)v.findViewById(R.id.dialog_cancel);
        right = (Button)v.findViewById(R.id.dialog_ok);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        this.addContentView(v, params);
        left.setOnClickListener(clickListener);
        right.setOnClickListener(clickListener);
    }
    
    private android.view.View.OnClickListener clickListener = new android.view.View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            cancel();
            switch (v.getId()) {
            case R.id.dialog_ok:
                if(confirmListener != null){
                    confirmListener.onClick(v, ClickType.Ok);
                }
                break;

            case R.id.dialog_cancel:
                if(confirmListener != null){
                    confirmListener.onClick(v, ClickType.Cancel);
                }
                break;
            }
        }
    };
    
    public void setOnDialogInfoConfirmListener(onDialogInfoConfirmListener confirmListener){
        this.confirmListener = confirmListener;
    }
    
    public interface onDialogInfoConfirmListener{
        void onClick(View v, ClickType type);
    }
}
