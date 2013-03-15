package com.xeodou.keydiary;

import org.holoeverywhere.app.Application;

public class MyApplication extends Application {

    private static MyApplication instance;
    public static MyApplication getInstance(){
        if (instance == null) {
            instance = new MyApplication();
        }
        return instance;
    }
    
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }

}
