package com.example.healthcare.NotificationsAndAlarm;

public class RemainderData {

    private Integer hour;
    private Integer minute;
    private String tag;

    public RemainderData(Integer hour, Integer minute, String tag) {
        this.hour = hour;
        this.minute = minute;
        this.tag = tag;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }




}
