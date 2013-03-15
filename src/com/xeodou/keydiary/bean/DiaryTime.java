package com.xeodou.keydiary.bean;

import com.xeodou.keydiary.Utils;

public class DiaryTime {

    int year;
    int month;
    int day;
    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }
    public int getMonth() {
        return month;
    }
    public void setMonth(int month) {
        this.month = month;
    }
    public int getDay() {
        return day;
    }
    public void setDay(int day) {
        this.day = day;
    }
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return year + "-" + Utils.douInt(month) +"-" + Utils.douInt(day); 
    }
    
    public String toTitle(){
        return year + "-" + Utils.douInt(month);
    }
}
