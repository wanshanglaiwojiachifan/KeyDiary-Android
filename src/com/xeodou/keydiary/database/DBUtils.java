package com.xeodou.keydiary.database;

import java.sql.SQLException;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.table.TableUtils;
import com.xeodou.keydiary.bean.Diary;

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
    
    public static boolean clearTables(Context context){
        if(dbHelper == null){
            dbHelper = getHelper(context);
        }
        try {
            TableUtils.clearTable(dbHelper.getConnectionSource(), Diary.class);
            releaseDB();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
}
