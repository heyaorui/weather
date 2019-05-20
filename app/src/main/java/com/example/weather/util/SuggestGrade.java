package com.example.weather.util;

import org.litepal.crud.DataSupport;

public class SuggestGrade extends DataSupport {

    private int id;

    private String type;

    private String brf;

    private String txt;

    private String term;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBrf() {
        return brf;
    }

    public void setBrf(String brf) {
        this.brf = brf;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    @Override
    public String toString() {
        return "SuggestGrade{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", brf='" + brf + '\'' +
                ", txt='" + txt + '\'' +
                ", term='" + term + '\'' +
                '}';
    }
}
