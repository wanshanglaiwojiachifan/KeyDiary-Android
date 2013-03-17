package com.xeodou.keydiary;

public class Config {

    //actions
    public final static String ACTION_ADD_DIARY_CURRENT = "com.xeodou.keydiary.add.current";
    public final static String ACTION_ADD_DIARY_OTHER = "com.xeodou.keydiary.add";
    public final static String ACTION_LOGIN = "com.xeodou.keydiary.login";
    public final static String ACTION_SELECT_LOGIN = "com.xeodou.keydiary.select.login";
    public final static String ACTION_SET = "com.xeodou.keydiary.set";
    public final static String ACTION_LOGOU = "com.xeodou.keydiary.logout";
    
    //status code
    public final static int FAIL_CODE = -0x000001;
    public final static int SUCCESSS_CODE = 0x00000;
    public final static int LOGIN_CODE = 0x00002;
    public final static int ADD_DAIRY_SUCCESS = 0x00003;
    public final static int DEL_DAIRY_SUCCESS = 0x00004;
    public final static int REQ_CODE = 0x00005;
    public final static int LOGOUT_CODE = 0x00006;
  
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
