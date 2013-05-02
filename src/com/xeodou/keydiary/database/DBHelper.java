package com.xeodou.keydiary.database;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.xeodou.keydiary.Config;
import com.xeodou.keydiary.bean.Diary;

public class DBHelper extends OrmLiteSqliteOpenHelper {

    private Dao<Diary, Integer> diaryDao = null;
    
    public DBHelper(Context context) {
        super(context, Config.DB_NAME , null, Config.DB_VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource cs) {
        // TODO Auto-generated method stub
        try {
            TableUtils.createTable(cs, Diary.class);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource cs, int oldVersion,
            int newVersion) {
        // TODO Auto-generated method stub
        try {
            TableUtils.dropTable(cs, Diary.class, true);
            onCreate(db, cs);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    
    
    @Override
    public void close() {
        // TODO Auto-generated method stub
        super.close();
        diaryDao = null;
    }

    public Dao<Diary, Integer> getDiaryDao() throws SQLException{
        if(diaryDao == null){
            diaryDao = getDao(Diary.class);
        }
        return diaryDao;
    }

}
