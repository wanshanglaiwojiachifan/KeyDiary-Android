package com.xeodou.keydiary.activity;

import com.xeodou.keydiary.Config;
import com.xeodou.keydiary.R;
import com.xeodou.keydiary.http.APIConfig;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebActivity extends Activity {

    private WebView webView;
    private ProgressDialog dialog;
    private View back_Btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        webView = (WebView)findViewById(R.id.webview);
        back_Btn = (View)findViewById(R.id.back_btn);
        dialog = new ProgressDialog(this);
        back_Btn.setOnClickListener(backClickListener);
        dialog.setMessage("正在加载中...");
        
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        
//        webView.loadUrl(APIConfig.API_BASIC + APIConfig.API_REGISTER_URI + "?redirecturl=" + APIConfig.API_CALLBACK);
        if(getIntent().getAction().equals(Config.ACTION_REGISTER)){
            webView.loadUrl(APIConfig.API_BASIC + APIConfig.API_REGISTER_URI );
        } else {
            webView.loadUrl(getIntent().getStringExtra("URL"));
        }
        
        webView.setWebViewClient(new WebViewClient(){

            private int index = 0;
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);
                if (dialog != null && !dialog.isShowing()) {
                    dialog.show();
                }
                if(url.equals(APIConfig.API_CALLBACK) && index == 0){
                    index ++;
                    finish();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onPageFinished(view, url);
                if(dialog != null && dialog.isIndeterminate()){
                    dialog.dismiss();
                }
            }
            
        });
    }
    
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        /**
         * Fix bug 
         * <link> http://crashes.to/s/de2f9a082cc</link>
         * */
        if(dialog != null)
            dialog.dismiss();
    }
    
    

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        /**
         * Fix bug 
         * <link> http://crashes.to/s/de2f9a082cc</link>
         * */
        if(dialog != null)
            dialog.dismiss();
        dialog = null;
    }



    private OnClickListener backClickListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            finish();
        }
    };

}
