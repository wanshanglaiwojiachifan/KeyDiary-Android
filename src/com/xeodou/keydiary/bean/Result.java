package com.xeodou.keydiary.bean;

import java.io.Serializable;

public class Result implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 7491274911L;
    
    Integer stat;

    public Integer getStat() {
        if(stat == null) stat = 7;
        return stat;
    }

    public void setStat(Integer stat) {
        this.stat = stat;
    }
    
}
