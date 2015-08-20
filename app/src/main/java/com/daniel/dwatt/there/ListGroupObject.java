package com.daniel.dwatt.there;

public class ListGroupObject {
    private String titleText;
    private String cityText;
    private boolean alarmOn;

    public ListGroupObject(String titleText, String cityText, boolean alarmOn) {
        this.titleText = titleText;
        this.cityText = cityText;
        this.alarmOn = alarmOn;
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
