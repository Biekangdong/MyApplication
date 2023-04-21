package com.example.customerlauncher3.bean;

public class EventUpdateDJCell {
    String control_dj_id;
    String MODE="";
    int connectMethod;
    int g_ledtype;
    String color="";


    public EventUpdateDJCell(String control_dj_id, String MODE, int connectMethod, int g_ledtype, String color) {
        this.control_dj_id = control_dj_id;
        this.MODE = MODE;
        this.connectMethod = connectMethod;
        this.g_ledtype = g_ledtype;
        this.color = color;
    }

    public String getControl_dj_id() {
        return control_dj_id;
    }

    public void setControl_dj_id(String control_dj_id) {
        this.control_dj_id = control_dj_id;
    }


    public String getMODE() {
        return MODE;
    }

    public void setMODE(String MODE) {
        this.MODE = MODE;
    }

    public int getConnectMethod() {
        return connectMethod;
    }

    public void setConnectMethod(int connectMethod) {
        this.connectMethod = connectMethod;
    }

    public int getG_ledtype() {
        return g_ledtype;
    }

    public void setG_ledtype(int g_ledtype) {
        this.g_ledtype = g_ledtype;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
