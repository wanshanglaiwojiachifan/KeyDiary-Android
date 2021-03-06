package com.xeodou.keydiary.activity;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.j256.ormlite.dao.Dao;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.xeodou.keydiary.Config;
import com.xeodou.keydiary.FontManager;
import com.xeodou.keydiary.FontManager.onFontCommpressListener;
import com.xeodou.keydiary.KeyDiaryResult;
import com.xeodou.keydiary.Log;
import com.xeodou.keydiary.R;
import com.xeodou.keydiary.UIHelper;
import com.xeodou.keydiary.Utils;
import com.xeodou.keydiary.UIHelper.ToastStyle;
import com.xeodou.keydiary.adapter.DiaryFragementAdapter;
import com.xeodou.keydiary.bean.Diary;
import com.xeodou.keydiary.bean.DiaryTime;
import com.xeodou.keydiary.bean.LoadDiary;
import com.xeodou.keydiary.bean.LoadUser;
import com.xeodou.keydiary.bean.Result;
import com.xeodou.keydiary.database.DBUtils;
import com.xeodou.keydiary.http.API;
import com.xeodou.keydiary.views.CustomDialog;
import com.xeodou.keydiary.views.CustomDialog.onDialogInfoConfirmListener;
import com.xeodou.keydiary.views.EditDialog;
import com.xeodou.keydiary.views.EditDialog.ClickType;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import group.pals.android.lib.ui.lockpattern.LockPatternActivity;
import group.pals.android.lib.ui.lockpattern.util.Settings;

public class MainActivity extends Activity {

    private final String TAG = "MainActivity";
    private ViewPager viewPager;
    private DiaryFragementAdapter adapter;
    private Map<String, Diary> diaries;
    private List<DiaryTime> titles;
    private View setBtn;
    private ProgressDialog dialog;
    private long startDate, endDate;
    private EditDialog editDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.update(this);
        setBtn = (View)findViewById(R.id.set_btn);
        setBtn.setOnClickListener(clickListener);
        diaries = new HashMap<String, Diary>();
        titles = new ArrayList<DiaryTime>();
        adapter = new DiaryFragementAdapter(diaries, titles);
        viewPager = (ViewPager)findViewById(R.id.diarycontent);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(pageChangeListener);
        if(!Utils.isLogin(this)){
            login();
            return;
        }
        init();
    }
    
    private void init(){
//        if(Utils.networkType() == Config.TYPE_NET_WORK_DISABLED) {
//            sendMsg(Config.FAIL_CODE, "您的网络有问题，请检查后重试！");
//            return;
//        }
//        new UnCompressTask().execute();
        char[] savedPattern = Settings.Security.getPattern(getApplicationContext());
        if (savedPattern != null) {
            Intent intent = new Intent(
                    LockPatternActivity.ACTION_COMPARE_PATTERN, null,
                    MainActivity.this, LockPatternActivity.class);
            intent.putExtra(LockPatternActivity.EXTRA_PATTERN,
                    savedPattern);
            startActivityForResult(intent, Config.LOCK_CODE_REQ);
        } else {
            loadAllDiaries();
        }
    }
    
    private class UnCompressTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            FontManager fontManager = new FontManager(MainActivity.this);
            fontManager.setOnFontCommpressListener(fontCommpressListener);
            fontManager.unCompressTar(); 
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
//            super.onPostExecute(result);
          sendMsg(Config.CODE_COMP, null);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            if (dialog == null) {
                dialog = ProgressDialog.show(MainActivity.this, null, "正在解压字体请等待...");
            }        
        }
        
    }
    
    private onFontCommpressListener fontCommpressListener = new onFontCommpressListener() {
        
        @Override
        public void onSuccess(File path) {
            // TODO Auto-generated method stub
//            dialog.setMessage("解压字体成功");
            Typeface typeface = Typeface.createFromFile(path);
            Utils.setTypeface(typeface);
        }
        
        @Override
        public void onStart() {
            // TODO Auto-generated method stub
            if (dialog == null) {
                dialog = ProgressDialog.show(MainActivity.this, null, "正在解压字体请等待...");
            }
        }
        
        @Override
        public void onFailed() {
            // TODO Auto-generated method stub
        }
    };
    
    private OnClickListener clickListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent intent = new Intent(MainActivity.this, SetActivity.class);
            intent.setAction(Config.ACTION_SET);
            startActivityForResult(intent, Config.REQ_CODE);
        }
    };
    
    private void checkLocal(){
        if(diaries.isEmpty()) return;
        try {
            Dao<Diary, Integer> diaryDao = DBUtils.getHelper(this).getDiaryDao();
            List<Diary> diaryList = diaryDao.queryForAll(); 
            if(!diaryList.isEmpty()){
                for(Diary d : diaryList){
                    if(diaries.containsKey(d.getD())){
                        if(Utils.getSignatue(d.getCreated()) > Utils.getSignatue(diaries.get(d.getD()).getCreated())){
                            diaries.put(d.getD(), d);
                        }
                    } else {
                        diaries.put(d.getD(), d);
                    }
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            UIHelper.show(this, "数据库异常", ToastStyle.Alert);
        }
    }
    
    private void getFiveMonth(){
        Date date;
        if(startDate <= 0) date = Utils.getDate();
        else date = Utils.getDate(startDate);
        int m = date.getMonth() + 1;
        int y = date.getYear() + 1900;
        Date endD;
        if(endDate <= 0) endD = Utils.getDate();
        else endD = Utils.getDate(endDate);
        int nm = endD.getMonth() + 1;
        int ny = Utils.getCurrentYear();
//        if(nm == m ) nm = m + 1;
        if(nm < m && ny > y) {
            nm += 12;
            ny --;
            if(ny < y) ny = y;
        } 
        int l = (ny - y) * 12 + nm - m + 1;
        for(int i = 0;i < l; i ++ ){
            DiaryTime time = new DiaryTime();
            time.setYear(y);
            time.setMonth(m);
            time.setDay(0);
            titles.add(time);
            m++;
            if(m > 12){
                m = m - 12;
                y += 1;
            }
        }
    }
    
    private void login(){
        if(!Utils.isLogin(this)){
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setAction(Config.ACTION_LOGIN);
            startActivityForResult(intent, Config.LOGIN_CODE);
        }
        return;
    }
    
    private void loadAllDiaries(){
        API.getALLDiaries(new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                // TODO Auto-generated method stub
                Log.d(TAG, response.toString());
                Gson gson = null;
                try {
                    gson = new Gson();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    sendMsg(Config.FAIL_CODE, "解析数据失败，请重试！");
                }
                LoadDiary result = gson.fromJson(response.toString() , LoadDiary.class);
                if (result.getStat() == 1 && result.getData() != null) {
                    List<Diary> ds = result.getData().getDiaries();
                    startDate = result.getData().getStartDate();
                    endDate = result.getData().getEndDate();
                    if(ds == null || ds.size() <= 0){
                        sendMsg(Config.SUCCESSS_CODE, "你还没有添加任何日记");
                        return;
                    }
                    Map<String, Diary> hashMap = new HashMap<String, Diary>();
                    for(Diary d : ds){
                        hashMap.put(d.getD(), d);
                    }
                    diaries.clear();
                    diaries.putAll(hashMap);
                    sendMsg(Config.SUCCESSS_CODE, null);
                } else {
                    sendMsg(Config.FAIL_CODE, null);
                }
            }

            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
                // TODO Auto-generated method stub
                String str = "未知错误";
                if(errorResponse != null) str = errorResponse.toString();
                sendMsg(Config.FAIL_CODE, str);
            }

            @Override
            public void onFailure(Throwable error) {
                // TODO Auto-generated method stub
//                super.onFailure(error);
                sendMsg(Config.FAIL_CODE, "连接服务器超时！");
            }

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                if (dialog == null) {
                    dialog = ProgressDialog.show(MainActivity.this, null, "正在加载日记...");
                } else {
                    dialog.setMessage("正在加载日记...");
                }
            }
            
        });
    } 
    
    private void sendMsg(int code , Object obj){
        Message msg = new Message();
        msg.what = code;
        msg.obj = obj;
        handler.sendMessage(msg);
    }
    
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            String str = null;
            if (msg.obj != null) str = msg.obj.toString();
            if(str == null || str.length() <= 0) str = "加载失败";
            if(msg.what == Config.CODE_COMP){
                loadAllDiaries();
                return;
            }

            if (msg.what == Config.AUTH_PASS) {
                /**
                 * Fix bug 
                 * <link> http://crashes.to/s/ee9ebb903a8 </link>
                 * */
                if(editDialog != null && editDialog.isShowing()) editDialog.dismiss();
                UIHelper.show(MainActivity.this, "验证成功正在跳转", ToastStyle.Info);
                clearSafePass();
                loadAllDiaries();
            } else if(msg.what == Config.AUTH_FAIL) {
                UIHelper.show(MainActivity.this, str, ToastStyle.Alert);
            }
            
            if(msg.what == Config.SUCCESSS_CODE){
                getFiveMonth();
                checkLocal();
                adapter.notifyDataSetChanged();
                viewPager.setCurrentItem(titles.size() - 1);
                UIHelper.show(MainActivity.this, "加载成功", ToastStyle.Info);

            } else if (msg.what == Config.FAIL_CODE)  {

//                UIHelper.show(MainActivity.this, str, ToastStyle.Alert);
                CustomDialog customDialog = new CustomDialog(MainActivity.this);
                customDialog.setDialogTitle("提示信息");
                customDialog.setDialogInfo("网络问题数据未加载，请重试！");
                customDialog.setLeftBtnText("算了");
                customDialog.setRightBtnText("确定");
                customDialog.setOnDialogInfoConfirmListener(new onDialogInfoConfirmListener() {
                    
                    @Override
                    public void onClick(View v, ClickType type) {
                        // TODO Auto-generated method stub
                        if(type.equals(ClickType.Cancel)) return;
                        loadAllDiaries();
                    }
                });
                customDialog.show();
            }
            if(dialog != null && dialog.isShowing() ) dialog.dismiss();
        }
        
    };
    
    private int last;
    private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {
        
        @Override
        public void onPageSelected(int position) {
            // TODO Auto-generated method stub
//            Toast.makeText(MainActivity.this, position + " position", Toast.LENGTH_SHORT).show();
            if((last > position) && position - 2 <= 0 ){
//                Toast.makeText(MainActivity.this, last - position +  "left", Toast.LENGTH_SHORT).show();
//                addPager(-1, position);
            } else if((last < position) && position + 2 == titles.size()) {
//                Toast.makeText(MainActivity.this, last - position + "right", Toast.LENGTH_SHORT).show();
//                addPager(1, position);
            }
            last = position;
        }
        
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // TODO Auto-generated method stub
//            Toast.makeText(MainActivity.this, position + "--" + positionOffset , Toast.LENGTH_SHORT).show();
        }
        
        @Override
        public void onPageScrollStateChanged(int state) {
            // TODO Auto-generated method stub
           if(state == ViewPager.SCROLL_STATE_SETTLING){
//               adapter.notifyDataSetChanged();
           }     
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Config.LOCK_CODE_REQ) {
            switch (resultCode) {
                case RESULT_OK:
                    // The user passed
                    loadAllDiaries();
                    break;
                case RESULT_CANCELED:
                    // The user cancelled the task
                    finish();
                    break;
                case LockPatternActivity.RESULT_FAILED:
                    // The user failed to enter the pattern
                    break;
            }

            /*
             * In any case, there's always a key EXTRA_RETRY_COUNT, which holds
             * the number of tries that the user did.
             */
            int retryCount = data.getIntExtra(
                    LockPatternActivity.EXTRA_RETRY_COUNT, 0);
            if(retryCount >= 3) {
                // 用密码找回
                CustomDialog customDialog = new CustomDialog(MainActivity.this);
                customDialog.setDialogTitle("忘记安全密码");
                customDialog.setDialogInfo("输入安全密码错误超过5次，\n是否通过密码找回？");
                customDialog.setLeftBtnText("算了");
                customDialog.setRightBtnText("确定");
                customDialog.setOnDialogInfoConfirmListener(new onDialogInfoConfirmListener() {

                    @Override
                    public void onClick(View v, ClickType type) {
                        // TODO Auto-generated method stub
                        if(type.equals(ClickType.Ok)){
                            editDialog = new EditDialog(MainActivity.this);
                            editDialog.getTitleText().setText("验证密码");
                            editDialog.setEditHint("请输入密码");
                            editDialog.setEditContent("");
                            editDialog.getEditContent().setTransformationMethod(PasswordTransformationMethod.getInstance());
                            editDialog.setOnDialogClickListener(new EditDialog.onDialogClickListener() {

                                @Override
                                public void onClick(ClickType type, final String content,
                                                    final String day, View v) {
                                    // TODO Auto-generated method stub
                                    if(!type.equals(ClickType.Delete)){
                                        if (!Utils.isLogin(MainActivity.this)) {
                                            clearSafePass();
                                            loadAllDiaries();
                                            return;
                                        }
                                        if (content.length() <= 0) return ;
                                        Config.password = content;
                                        vertifyUser();
                                    } else {
                                        setBtn.setVisibility(View.GONE);
                                        closeSelf();
                                    }
                                }
                            });
                            editDialog.show();

                        } else {
                            closeSelf();
                        }
                    }
                });
                customDialog.show();

            }
        } else {
            if(resultCode == Config.FAIL_CODE){
                finish();
            } else if(resultCode == Config.LOGIN_CODE) {
                init();
            } else if(resultCode == Config.LOGOUT_CODE){
                finish();
            }
        }
    }

    private void  vertifyUser() {
        API.getUser(new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(JSONObject response) {
                // TODO Auto-generated method stub

                Gson gson = new Gson();
                LoadUser user = gson.fromJson(response.toString(), LoadUser.class);
                if (user.getStat() == 1 && user != null) {
//                    (new Utils()).storeUser(LoginActivity.this, user.getData());
                    sendMsg(Config.AUTH_PASS, null);
                }
            }

            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
                // TODO Auto-generated method stub
                Gson gson = new Gson();
                try {
                    Result result = gson.fromJson(errorResponse.toString(), Result.class);
                    if(result == null) {
                        sendMsg(Config.AUTH_FAIL, "未知问题");
                        return;
                    }
                    sendMsg(Config.AUTH_FAIL, KeyDiaryResult.getMsg(result.getStat()));
                } catch (JsonSyntaxException e1) {
                    // TODO Auto-generated catch block
                    sendMsg(Config.AUTH_FAIL, "验证失败");
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                // TODO Auto-generated method stub
                sendMsg(Config.AUTH_FAIL, content);
            }

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                showDialog(getResources().getString(R.string.loading));
            }

        });
    }

    private void showDialog(String msg){
        if (dialog == null) {
            dialog = new ProgressDialog(this);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.setMessage(msg);
        }
        dialog.show();
    }

    private void dismissDialog(){
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private void closeSelf() {
        Toast.makeText(MainActivity.this, "程序即将关闭。", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                finish();
            }
        }, 2000);
    }

    private void clearSafePass() {
        Settings.Security.setPattern(getApplicationContext(), null);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        DBUtils.releaseDB();
    }

}
