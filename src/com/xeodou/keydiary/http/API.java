package com.xeodou.keydiary.http;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class API {

    public static void getALLDiaries(AsyncHttpResponseHandler responseHandler){
        KeyDiaryRequest.get(APIConfig.API_GET_ALL, null, responseHandler);
    }
    
    public static void getUser(AsyncHttpResponseHandler responseHandler){
        KeyDiaryRequest.get(APIConfig.API_ACCOUNT_LOGIN, responseHandler);
    }
    
    public static void getDiaryByDay(String date, AsyncHttpResponseHandler responseHandler){
        KeyDiaryRequest.get(APIConfig.API_GET_ALL + "/" + date, null, responseHandler);
    }
    
    public static void getDiaryFromTo(String from, String to, AsyncHttpResponseHandler responseHandler){
        KeyDiaryRequest.get(APIConfig.API_GET_ALL + "/" + from + "/" + to, null, responseHandler);
    }
    
    public static void addDiary(String date,String content, AsyncHttpResponseHandler responseHandler){
        RequestParams params = new RequestParams();
        params.put("d", date);
        params.put("content", content);
        KeyDiaryRequest.post(APIConfig.API_ADD_DIARY, params, responseHandler);
    }
    
    public static void updateDiary(String data,String content, AsyncHttpResponseHandler responseHandler){
        RequestParams params = new RequestParams();
        params.put("d", data);
        params.put("content", content);
        KeyDiaryRequest.post(APIConfig.API_UPDATE_DIARY, params, responseHandler);
    }
    
    public static void deleteDiary(String data, AsyncHttpResponseHandler responseHandler){
        RequestParams params = new RequestParams();
        params.put("d", data);
        KeyDiaryRequest.post(APIConfig.API_DELETE_DIARY, params, responseHandler);
    }
    
    public static void upsertDiary(String date, String content, AsyncHttpResponseHandler responseHandler){
        RequestParams params = new RequestParams();
        params.put("d", date);
        params.put("content", content);
        KeyDiaryRequest.post(APIConfig.API_UPSERT_DIARY, params, responseHandler);
    }
}
