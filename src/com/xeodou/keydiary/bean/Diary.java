package com.xeodou.keydiary.bean;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;

public class Diary implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 7391239011L;
    
    @DatabaseField(generatedId = true)
    int id;
    @DatabaseField
    String did;
    @DatabaseField
    String d;
    @DatabaseField
    String content;
    @DatabaseField
    String created;
    @DatabaseField
    Boolean isLocal;
    int sid;
    

    public Diary() {
        // TODO Auto-generated constructor stub
        this.isLocal = false;
    }
    
    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }
    
    public Boolean getIsLocal() {
        return isLocal;
    }

    public void setIsLocal(Boolean isLocal) {
        this.isLocal = isLocal;
    }

    public String getCreated() {
        return created;
    }
    public void setCreated(String created) {
        this.created = created;
    }
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
