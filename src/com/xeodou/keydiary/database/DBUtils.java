package com.xeodou.keydiary.database;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

public class DBUtils {

    private static DBHelper dbHelper = null;
    
    public static DBHelper getHelper(Context context){
        if(dbHelper == null)
            dbHelper = (DBHelper)OpenHelperManager.getHelper(context, DBHelper.class);
        return dbHelper;
    }
    
    public static void releaseDB(){
        if(dbHelper != null){
            dbHelper.close();
            OpenHelperManager.releaseHelper();
            dbHelper = null;
        }
    }
}
