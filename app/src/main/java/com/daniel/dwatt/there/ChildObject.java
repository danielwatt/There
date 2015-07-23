package com.daniel.dwatt.there;

public class ChildObject {
    private String addressText;
    private String ringToneText;
    private boolean vibrateOn;
    private boolean repeatOn;


    public ChildObject (String addressText, String ringToneText, boolean repeatOn, boolean vibrateOn)
    {
        this.addressText = addressText;
        this.ringToneText = ringToneText;
        this.repeatOn = repeatOn;
        this.vibrateOn = vibrateOn;
    }

    public String getAddressText() {

        return addressText;
    }

    public boolean isRepeatOn() {

        return repeatOn;
    }

    public String getRingToneText() {

        return ringToneText;
    }

    public boolean isVibrateOn() {
        return vibrateOn;
    }

    public void setVibrateOn(boolean vibrateOn) {
        this.vibrateOn = vibrateOn;
    }

    public void setAddressText(String addressText) {

        this.addressText = addressText;
    }

    public void setRepeatOn(boolean repeatOn) {

        this.repeatOn = repeatOn;
    }

    public void setRingToneText(String ringToneText) {

        this.ringToneText = ringToneText;
    }
}
