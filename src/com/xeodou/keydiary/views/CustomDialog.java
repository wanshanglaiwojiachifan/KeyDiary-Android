package com.xeodou.keydiary.views;

import com.xeodou.keydiary.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class CustomDialog extends Dialog {

    private Context context;
    private TextView info;
    private onDialogInfoConfirmListener confirmListener;
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
        }
    }
    
    private void initView(){
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_info, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        info = (TextView)v.findViewById(R.id.dialog_content);
        View cancel = v.findViewById(R.id.dialog_cancel);
        View ok = v.findViewById(R.id.dialog_ok);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        this.addContentView(v, params);
        cancel.setOnClickListener(clickListener);
        ok.setOnClickListener(clickListener);
    }
    
    private android.view.View.OnClickListener clickListener = new android.view.View.OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
            case R.id.dialog_ok:
                if(confirmListener != null){
                    confirmListener.onClick(v);
                }
                break;

            default:
                break;
            }
            cancel();
        }
    };
    
    public void onDialogInfoConfirmListener(onDialogInfoConfirmListener confirmListener){
        this.confirmListener = confirmListener;
    }
    
    public interface onDialogInfoConfirmListener{
        void onClick(View v);
    }
}
