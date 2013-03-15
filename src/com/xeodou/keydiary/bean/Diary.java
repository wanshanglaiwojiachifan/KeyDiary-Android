package com.xeodou.keydiary.bean;

import java.io.Serializable;

public class Diary implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 7391239011L;
    
    String did;
    String d;
    String content;
    public String getDid() {
        return did;
    }
    public void setDid(String did) {
        this.did = did;
    }
    public String getD() {
        return d;
    }
    public void setD(String d) {
        this.d = d;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    
}
