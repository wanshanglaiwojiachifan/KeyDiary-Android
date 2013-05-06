package com.xeodou.keydiary;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(formKey = "73d75a672a7c9d8320956a2fd84f2d91")
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
        ACRA.init(this);
        ACRA.getErrorReporter().setReportSender(new HockeySender());
     
        super.onCreate();
    }

}
