package com.xeodou.keydiary.bean;

import java.util.List;

public class LoadDiary extends Result {

    /**
     * 
     */
    private static final long serialVersionUID = 804329432941L;

    List<Diary> data;

    public List<Diary> getData() {
        return data;
    }

    public void setData(List<Diary> data) {
        this.data = data;
    }
    
}
