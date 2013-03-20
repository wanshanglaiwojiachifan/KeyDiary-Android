package com.xeodou.keydiary;

import com.xeodou.keydiary.KeyboardDetectorLineayLayout.IKeyboardChanged;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class PanningEditText extends EditText implements IKeyboardChanged{

    public PanningEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }
    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent keyEvent){
      if(keyCode == KeyEvent.KEYCODE_BACK)
      {
        clearFocus();
      }
      return super.onKeyPreIme(keyCode, keyEvent);
    }
    @Override
    public void onKeyboardShown() {
        // TODO Auto-generated method stub
        setCursorVisible(true);
    }
    @Override
    public void onKeyboardHidden() {
        // TODO Auto-generated method stub
        setCursorVisible(false);
        clearFocus();
    }
}
