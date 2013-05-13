package com.xeodou.keydiary;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tools.bzip2.CBZip2InputStream;
import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarInputStream;

import android.content.Context;
import android.os.Environment;

public class FontManager {

    private Context context;
    private final int BUFFER_SIZE = 2048;
    private onFontCommpressListener fontCommpressListener;
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
        if(fontCommpressListener != null) fontCommpressListener.onStart();
        File tar = unCompressBz2();
        if(tar != null){
            try {
                File ttf = new File(getFontPath(), "FZLTHJW.TTF");
                if(ttf.exists()){
                    if(MD5Util.getFileMD5String(ttf).equals("c17e4d10afb6dbd26fb3fe6dfe9a3f15")){
                        return ttf.getPath();
                    }
                    ttf.delete();
                }
                FileInputStream in = new FileInputStream(tar);
                TarInputStream tain = new TarInputStream(new BufferedInputStream(in));
                TarEntry entry;
                while((entry = tain.getNextEntry()) != null) {
                   int count;
                   byte data[] = new byte[BUFFER_SIZE];

                   FileOutputStream fos = new FileOutputStream(getFontPath().getPath() + "/" + entry.getName());
                   BufferedOutputStream dest = new BufferedOutputStream(fos);

                   while((count = tain.read(data)) != -1) {
                      dest.write(data, 0, count);
                   }

                   dest.flush();
                   dest.close();
                }
                
                tain.close();
                if(fontCommpressListener != null) fontCommpressListener.onSuccess(ttf);
                return ttf.getPath();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if(fontCommpressListener != null) fontCommpressListener.onFailed();
        return null;
    }
    
    private File unCompressBz2(){
        File appDir = null;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            appDir = getFontPath();
            try {
                File tarFile = new File(appDir, "FZLTHJW.tar");
                if(tarFile.exists()) {
                    if(MD5Util.getFileMD5String(tarFile).equals("b7458e6efa9f4782f6a63dc4e6fb8219")){
                        return tarFile;
                    }
                    tarFile.delete();
                }
                InputStream in = context.getAssets().open("FZLTHJW.tar.bz2");
                if(in == null) return null;
                FileOutputStream out = new FileOutputStream(tarFile);
                try {
                    CBZip2InputStream cbin = new CBZip2InputStream(in);
                    final byte[] buffer = new byte[BUFFER_SIZE];
                    int n = 0;
                    while (-1 != (n = cbin.read(buffer))) {
                      out.write(buffer, 0, n);
                    }
                    out.close();
                    cbin.close();
                    return tarFile;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }
    
    public void setOnFontCommpressListener(onFontCommpressListener fontCommpressListener){
        this.fontCommpressListener = fontCommpressListener;
    }
    
    public interface onFontCommpressListener{
        void onStart();
        void onSuccess(File path);
        void onFailed();
    }
    
    
}
