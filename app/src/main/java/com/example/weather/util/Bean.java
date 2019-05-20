package com.example.weather.util;


public class Bean {

    private int id;
    private String brf;
    private String txt;
    private String term;
    private Suggest suggest;
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public void setBrf(String brf) {
        this.brf = brf;
    }
    public String getBrf() {
        return brf;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }
    public String getTxt() {
        return txt;
    }

    public void setTerm(String term) {
        this.term = term;
    }
    public String getTerm() {
        return term;
    }

    public void setSuggest(Suggest suggest) {
        this.suggest = suggest;
    }
    public Suggest getSuggest() {
        return suggest;
    }

}