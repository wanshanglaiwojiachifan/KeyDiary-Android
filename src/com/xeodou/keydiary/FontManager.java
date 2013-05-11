package com.xeodou.keydiary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tools.bzip2.CBZip2InputStream;
import org.kamranzafar.jtar.TarInputStream;

import android.content.Context;
import android.os.Environment;

public class FontManager {

    private Context context;
    private final int BUFFER_SIZE = 1024;
    public FontManager() {
        // TODO Auto-generated constructor stub
    }
    
    public FontManager(Context context){
        this.context = context;
//        context.getAssets().open("/fonts/FZLTHJW.tar.bz2";
    }
    
    private File getFontPath(){
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File appDir = new File(new File(dataDir, context.getPackageName()), "font");
        if(!appDir.exists()){
            if(!appDir.mkdirs()){
                return null;
            }
        }
        return appDir;
    }
    
    public String unCompressTar(){
        File tar = unCompressBz2();
        if(tar != null){
            try {
                File ttf = new File(getFontPath(), "FZLTHJW.TTF");
                FileInputStream in = new FileInputStream(tar);
                FileOutputStream out = new FileOutputStream(ttf);
                TarInputStream tain = new TarInputStream(in);
                final byte[] buffer = new byte[BUFFER_SIZE];
                int n = 0;
                while (-1 != (n = tain.read(buffer))) {
                  out.write(buffer, 0, n);
                }
                out.close();
                tain.close();
                return ttf.getPath();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }
    
    private File unCompressBz2(){
        File appDir = null;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            appDir = getFontPath();
            try {
                File tarFile = new File(appDir, "FZLTHJW.tar");
                InputStream in = context.getAssets().open("/fonts/FZLTHJW.tar.bz2");
                FileOutputStream out = new FileOutputStream(tarFile);
                CBZip2InputStream cbin = new CBZip2InputStream(in);
                final byte[] buffer = new byte[BUFFER_SIZE];
                int n = 0;
                while (-1 != (n = cbin.read(buffer))) {
                  out.write(buffer, 0, n);
                }
                out.close();
                cbin.close();
                return tarFile;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }
}
