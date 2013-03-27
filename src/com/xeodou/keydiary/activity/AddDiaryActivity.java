package com.xeodou.keydiary.activity;

import com.xeodou.keydiary.Config;
import com.xeodou.keydiary.R;
import com.xeodou.keydiary.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

public class AddDiaryActivity extends Activity {

    private TextView dayTv;
    private TextView monthTv;
    private EditText diaryEtv;
    private TextView yearTv;
    private String action;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adddiary);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dayTv = (TextView)findViewById(R.id.day_tv);
        monthTv = (TextView)findViewById(R.id.month_tv);
        yearTv = (TextView)findViewById(R.id.year_tv);
        diaryEtv = (EditText)findViewById(R.id.diary_etv);
        
        action = getIntent().getAction();
        if(action != null && action.equals(Config.ACTION_ADD_DIARY_CURRENT)){
            dayTv.setText(Utils.getCurrentDay() + "");
            monthTv.setText(Utils.getCurrentMonth() + "月");
            yearTv.setText(Utils.getCurrentYear() + "年");
        } else {
            
        }
        
    }

}
