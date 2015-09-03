package com.daniel.dwatt.there;

public class ListGroupObject {
    private String titleText;
    private String cityText;
    private boolean alarmOn;
    private Alarm alarm;

    public ListGroupObject(String titleText, String cityText, boolean alarmOn, Alarm alarm) {
        this.titleText = titleText;
        this.cityText = cityText;
        this.alarmOn = alarmOn;
        this.alarm = alarm;
    }
    public Alarm getAlarm() {
        return alarm;
    }

    public boolean isAlarmOn() {
        return alarmOn;
    }

    public String getCityText() {
        return cityText;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setAlarm(Alarm alarm) {
        this.alarm = alarm;
    }

    public void setAlarmOn(boolean alarmOn) {
        this.alarmOn = alarmOn;
    }

    public void setCityText(String cityText) {
        this.cityText = cityText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

}
