package com.xeodou.keydiary.activity;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.xeodou.keydiary.Config;
import com.xeodou.keydiary.R;
import com.xeodou.keydiary.Utils;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.TextView;

import android.os.Bundle;
import android.view.WindowManager;

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
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Used to put dark icons on light action bar

        menu.add("删除").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.add("添加").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //This uses the imported MenuItem from ActionBarSherlock
//        Toast.makeText(this, "Got click: " + item.toString(), Toast.LENGTH_SHORT).show();
        return true;
    }
}
