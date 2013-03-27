package com.xeodou.keydiary.bean;

import java.util.List;

public class DiaryResult extends Result{

    /**
     * 
     */
    private static final long serialVersionUID = 4134141241L;

    Long endDate;
    Long startDate;
    List<Diary> diaries;
    String startDateFormat;
    String endDateFormat;
    public String getStartDateFormat() {
        return startDateFormat;
    }
    public void setStartDateFormat(String startDateFormat) {
        this.startDateFormat = startDateFormat;
    }
    public String getEndDateFormat() {
        return endDateFormat;
    }
    public void setEndDateFormat(String endDateFormat) {
        this.endDateFormat = endDateFormat;
    }
    public Long getEndDate() {
        return endDate;
    }
    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }
    public Long getStartDate() {
        return startDate;
    }
    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }
    public List<Diary> getDiaries() {
        return diaries;
    }
    public void setDiaries(List<Diary> diaries) {
        this.diaries = diaries;
    }
}
