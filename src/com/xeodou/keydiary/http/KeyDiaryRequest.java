package com.xeodou.keydiary.http;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.xeodou.keydiary.Config;

public class KeyDiaryRequest {

    private final static String BASE_URL = APIConfig.API_BASIC;
    
    private static AsyncHttpClient client = new AsyncHttpClient();
    static {
        if (!Config.username.equals("") && !Config.password.equals("")) {
            client.setBasicAuth(Config.username, Config.password);
        }
    }
    public static void get(String uri,RequestParams params, AsyncHttpResponseHandler responseHandler){
        client.get(getUri(uri), params, responseHandler);
    }
    
    public static void post(String uri, RequestParams params, AsyncHttpResponseHandler responseHandler){
        client.post(getUri(uri), params, responseHandler);
    }
    
    public static void put(String uri, RequestParams params, AsyncHttpResponseHandler responseHandler){
        client.put(uri, params, responseHandler);
    }
    
    public static void delete(String uri, AsyncHttpResponseHandler responseHandler){
        client.delete(uri, responseHandler);
    }
    
    private static String getUri(String uri){
        return BASE_URL + uri + APIConfig.API_AUTH;
    }
}
