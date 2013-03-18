package com.xeodou.keydiary.bean;

public class LoadADiary extends Result {

    /**
     * 
     */
    private static final long serialVersionUID = 70328401L;
    
    Diary data;

    public Diary getData() {
        return data;
    }

    public void setData(Diary data) {
        this.data = data;
    }
    
}
