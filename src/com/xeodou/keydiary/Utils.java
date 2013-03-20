package com.xeodou.keydiary;

import java.util.Date;

import com.xeodou.keydiary.bean.User;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Utils {

    private static Date date = null;
    
    public static boolean isLogin(Context context){
        boolean isLogin = false;
        //to do something
        if(!((new Utils()).getPass(context)).equals("")){
            isLogin = true;
        }
        return isLogin;
    }
    
    public void storePass(Context context,String user, String pass){
        SharedPreferences preferences = context.getSharedPreferences(Config.PREF_NAME_LOGIN, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString(Config.USER+"", user);
        editor.putString(Config.PASS, pass);
        editor.commit();
    }
    public void storeKey(Context context,String key, String value){
        SharedPreferences preferences = context.getSharedPreferences(Config.PREF_NAME_LOGIN, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
    
    public void storeUser(Context context,User user){
        SharedPreferences preferences = context.getSharedPreferences(Config.PREF_NAME_USER, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putLong(Config.UID, user.getUid());
        editor.putString(Config.EMAIL, user.getEmail());
        editor.putString(Config.NAME, user.getUsername());
        editor.commit();
    }
    
    public void storeAlerm(Context context,String time){
        SharedPreferences preferences = context.getSharedPreferences(Config.PREF_NAME_USER, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString("ALERM", time);
        editor.commit();
    }
    
    public String getAlerm(Context context){
        SharedPreferences preferences = context.getSharedPreferences(Config.PREF_NAME_USER, Context.MODE_PRIVATE);
        return preferences.getString("ALERM", "");
    }
    
    public String getPass(Context context){
        SharedPreferences preferences = context.getSharedPreferences(Config.PREF_NAME_LOGIN, Context.MODE_PRIVATE);
        String user = preferences.getString(Config.USER, "");
        Config.username = user;
        String pass = preferences.getString(Config.PASS, "");
        Config.password = pass;
        return user + pass;
    }
    
    public static String getStringById(int id){
        String str = "";
        if(id > 0) str = MyApplication.getInstance().getResources().getString(id);
        return str;
    }
   
   public static int getDayOfMonth(int year, int month){
       if(month == 4 || month == 6 || month == 9 || month == 11 ){
           return 30;
       } else if(month == 2){
           return isLeapYear(year) ? 29 : 28;
       } else {
           return 31;
       }
   }
   
   public static boolean isLeapYear(int year)
   {
       return ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) ;
   }
   
   public static String douInt(int a){
       if(a == 0){
           return "";
       } else if(a < 10) {
           return "0" + a;
       } else {
           return a +"";
       }
   }
   
   public static int getCurrentYear(){
       return getDate().getYear() + 1900;
   }
   
   public static int getCurrentMonth(){
       return getDate().getMonth() + 1;
   }
   
   public static int getCurrentDay(){
       return getDate().getDate();
   }
   
   public static int getCurrentHour(){
       return getDate().getHours();
   }
   public static int getCurrentMin(){
       return getDate().getMinutes();
   }
   
   public static Date getDate(long time){
       return new Date(time);
   }
   private static Date getDate(){
       if(date != null){
           return date;
       }
       return new Date();
   }
}
