package com.xeodou.keydiary;

import com.xeodou.keydiary.KeyboardDetectorLineayLayout.IKeyboardChanged;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class PanningEditText extends EditText implements IKeyboardChanged{

    private Context context;
    public PanningEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        this.context = context;
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
//        setCursorVisible(true);
//        setBackgroundResource(R.drawable.edit_text_s);
//        requestFocus();
    }
    @Override
    public void onKeyboardHidden() {
        // TODO Auto-generated method stub
//        setCursorVisible(false);
        clearFocus();
//        setBackgroundResource(R.drawable.edit_text_n);
    }
    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        // Checks whether a hardware keyboard is available
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            Toast.makeText(context, "keyboard visible", Toast.LENGTH_SHORT).show();
        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            Toast.makeText(context, "keyboard hidden", Toast.LENGTH_SHORT).show();
        }
        super.onConfigurationChanged(newConfig);
    }
    @Override
    public void onEditorAction(int actionCode) {
        // TODO Auto-generated method stub
        if(actionCode == EditorInfo.IME_ACTION_DONE){
            ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                    getWindowToken(), 0);
            clearFocus();
        }
        super.onEditorAction(actionCode);
    }
    @Override
    protected void onTextChanged(CharSequence text, int start,
            int lengthBefore, int lengthAfter) {
        // TODO Auto-generated method stub
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (calculateLength(text) > 7) {
//            if(editText.isFocused()){
                this.setBackgroundResource(R.drawable.edit_text_e);
//            }
        } else {
            if(isSelected() || isFocused()){
                setBackgroundResource(R.drawable.edit_text_s);
            } else {
                setBackgroundResource(R.drawable.edit_text_n);
            }
        }
    }
    
    @Override
    protected void onFocusChanged(boolean focused, int direction,
            Rect previouslyFocusedRect) {
        // TODO Auto-generated method stub
        if(focused) setBackgroundResource(R.drawable.edit_text_s);
        else if(calculateLength(getText().toString()) < 7) setBackgroundResource(R.drawable.edit_text_n);
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
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
}
