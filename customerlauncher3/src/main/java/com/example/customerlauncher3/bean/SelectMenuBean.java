package com.example.customerlauncher3.bean;

public class SelectMenuBean {
    String name;
    String subname;
    int textColor;

    private boolean isChecked;

    public SelectMenuBean(String name, int textColor) {
        this.name = name;
        this.textColor = textColor;
    }

    public SelectMenuBean(String name, String subname, int textColor) {
        this.name = name;
        this.subname = subname;
        this.textColor = textColor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubname() {
        return subname;
    }

    public void setSubname(String subname) {
        this.subname = subname;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
