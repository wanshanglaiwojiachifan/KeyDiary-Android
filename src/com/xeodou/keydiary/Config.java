package com.xeodou.keydiary;

public class Config {

    //actions
    public final static String ACTION_ADD_DIARY_CURRENT = "com.xeodou.keydiary.add.current";
    public final static String ACTION_ADD_DIARY_OTHER = "com.xeodou.keydiary.add";
    public final static String ACTION_LOGIN = "com.xeodou.keydiary.login";
    
    //status code
    public final static int FAIL_CODE = -0x000001;
    public final static int SUCCESSS_CODE = 0x00000;
    public final static int LOGIN_CODE = 0x00002;
    public final static int ADD_DAIRY_SUCCESS = 0x00003;
    public final static int DEL_DAIRY_SUCCESS = 0x00004;
  
    //user pass
    public static String username = "";
    public static String password = "";
    public final static String USER = "USER";
    public final static String PASS = "PASS";
    public final static String UID = "UID";
    public final static String EMAIL = "EMAIL";
    public final static String NAME = "NAME";
    
    //sharedpreference
    public final static String PREF_NAME_LOGIN = "keydiary";
    public final static String PREF_NAME_USER = "keydiary";
}
