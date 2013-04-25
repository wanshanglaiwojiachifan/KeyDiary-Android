package com.xeodou.keydiary;

public enum KeyDiaryResult {

    UNKNOWN(0, "未知错误"),
    SUCCEED(1, "成功"),
    DATABASE_ERR(2, "数据库出错"),
    UNAUTHORIZED(3, "用户名或者密码错误"),
    ILLEGAL_PARAMETER(4, "参数错误"),
    ILLEGAL_APP(5, "不合法的应用"),
    DUPLICATE(6, "该记录已存在"),
    ERROR(7, "出现错误"),
    PARAM_EMPTY(8, "请完整填写信息"),
    ACCOUNT_WRONG(9, "邮箱或密码错误"),
    NO_LOGIN(10, "您还没有登录"),
    EMAIL_ILLEGAL(11, "邮箱格式不正确"),
    EMAIL_EXIST(12, "该邮箱已被占用了"),
    EMAIL_NOT_FOUND(13, "该用户不存在"),
    PASSW_ILLEGAL(14, "密码格式不正确"),
    CONFIRM_NOT_MATCH(15, "两次输入的密码不一样"),
    ORIGINAL_WRONG(16, "原密码不正确"),
    SUCCEED_REG(17, "注册成功，请登录"),
    SUCCEED_UPDATE(18, "修改成功"),
    SUCCEED_RESETPWD(19, "重设密码成功，请登录"),
    MAIL_SEND_FAILED(20, "邮件发送失败"),
    FREQ_TOO_FAST(21, "频率太快，请休息一下再尝试"),
    VER_CODE_ILLEGAL(22, "无效的验证码"),
    VER_CODE_EXPIRED(23, "验证码已过期"),
    ACCOUNT_ILLEGAL(24, "请输入注册邮箱"),
    CONTENT_TOO_LONG(25, "日记内容太长了"),
    CONTENT_EMPTY(26, "日记内容不能为空"),
    DATE_FUTURE(27, "不能写未来的日记");
    
    
    private int code;
    private String msg;
    
    KeyDiaryResult(int code, String msg){
        this.code = code;
        this.msg = msg;
    }
    
    int getCode(){
        return this.code;
    }
    
    String getMsg(){
        return this.msg;
    }
    
    public static String getMsg(int code){
        for (KeyDiaryResult result : KeyDiaryResult.values()){
            if(result.getCode() == code){
                return result.getMsg();
            }
        }
        return "";
    }
}
