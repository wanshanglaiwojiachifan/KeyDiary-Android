package com.xeodou.keydiary;

import android.content.Context;
import android.graphics.Paint.Join;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class UIHelper {
    public static final int holoRedLight = 0xffff4444;
    public static final int holoGreenLight = 0xff99cc00;
    public static final int holoBlueLight = 0xff33b5e5;

    public enum ToastStyle {
        Alert, Confirm, Info;
    }
    
    public static void show(Context context, String text, ToastStyle style){
        View v = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
        switch (style) {
        case Alert:
            v.setBackgroundColor(holoRedLight);
            break;

        case Confirm:
            v.setBackgroundColor(holoGreenLight);
            break;
        case Info:
            v.setBackgroundColor(holoBlueLight);
            break;
        }
        ((TextView)v.findViewById(R.id.toast_content)).setText(text);
        Toast toast = new Toast(MyApplication.getInstance().getApplicationContext());
        toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(v);
        toast.show();
    }
    
}
