package com.xeodou.keydiary.bean;

import java.util.List;

public class LoadDiary extends Result {

    /**
     * 
     */
    private static final long serialVersionUID = 804329432941L;

    DiaryResult data;

    public DiaryResult getData() {
        return data;
    }

    public void setData(DiaryResult data) {
        this.data = data;
    }
}
