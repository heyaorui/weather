package com.example.weather.util;

import org.litepal.crud.DataSupport;

public class Suggest extends DataSupport {

    private int id;
    private String name;
    private boolean selected;



    public Suggest() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
