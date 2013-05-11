package com.xeodou.keydiary;

public class Config {

    //actions
    public static final String ACTION_ADD_DIARY_CURRENT = "com.xeodou.keydiary.add.current";
    public static final String ACTION_ADD_DIARY_OTHER = "com.xeodou.keydiary.add";
    public static final String ACTION_LOGIN = "com.xeodou.keydiary.login";
    public static final String ACTION_SELECT_LOGIN = "com.xeodou.keydiary.select.login";
    public static final String ACTION_SET = "com.xeodou.keydiary.set";
    public static final String ACTION_LOGOUT = "com.xeodou.keydiary.logout";
    public static final String ACTION_LOADING = "com.xeodou.keydiary.loaging";
    public static final String ACTION_REGISTER = "com.xeodou.keydiary.register";
    public static final String ACTION_FEEDBACK = "com.xeodou.keydiary.feedback";
    
    //status code
    public static final int FAIL_CODE = -0x000001;
    public static final int SUCCESSS_CODE = 0x00000;
    public static final int LOGIN_CODE = 0x00002;
    public static final int ADD_DAIRY_SUCCESS = 0x00003;
    public static final int DEL_DAIRY_SUCCESS = 0x00004;
    public static final int REQ_CODE = 0x00005;
    public static final int LOGOUT_CODE = 0x00006;
    public static final int FAIL_ADD = 0x00007;
    public static final int FAIL_UPDATE = 0x00008;
    public static final int FAIL_DELETE = 0x00009;
    public static final int FAIL_UPSERT = 0x00010;
    public static final int SUCCESS_UPSERT = 0x00011;
    public static final int FAIL_TO_LONG = 0x00012;
    public static final int SUCCESS_REGISTER = 0x00013;
    public static final int SUCCESS_DELETE = 0x00014;
  
    //user pass
    public static String username = "";
    public static String password = "";
    public static final String USER = "USER";
    public static final String PASS = "PASS";
    public static final String UID = "UID";
    public static final String EMAIL = "EMAIL";
    public static final String NAME = "NAME";
    
    //sharedpreference
    public static final String PREF_NAME_LOGIN = "keydiary";
    public static final String PREF_NAME_USER = "keydiary";
    
    //alerm id
    public static final int ALERM_ID = 0x000100;
    
    //database
    public static final String DB_NAME = "keydiary.db";
    public static final int DB_VERSION = 1;
    
    //net state
    public static final int TYPE_NET_WORK_DISABLED = 0; //网路不可用
    public static final int TYPE_MNO_CM = 1;//移动网路
    public static final int TYPE_MNO_CU = 2;//联通网络
    public static final int TYPE_MNO_CT = 3;//电信网络
    public static final int TYPE_CM_CU_WAP = 4;//移动联通wap
    public static final int TYPE_CT_WAP = 5;//电信wap
    public static final int TYPE_OTHER_NET = 6;//移动联通电信NET网络
    public static final int TYPE_WIFI = 7;//wifi网络
    public static int CURRENT_NET_WORK_TYPE = TYPE_OTHER_NET;//
    
}
