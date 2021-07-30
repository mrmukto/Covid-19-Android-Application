package com.shoutlab.coronabd.Helpline;

public class HelplineItems {

    private String area, number, details;

    public HelplineItems(String area, String number, String details) {
        this.area = area;
        this.number = number;
        this.details = details;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
