package com.example.weather.gson;

import java.util.List;

public class Weather {
    private Basic basic;

    private Update update;

    private String status;

    private Now now;
    private List<Sunrise_sunset> sunrise_sunset ;
    private List<Daily_forecast> daily_forecast ;

    private List<Lifestyle> lifestyle ;

    public void setBasic(Basic basic){
        this.basic = basic;
    }
    public Basic getBasic(){
        return this.basic;
    }
    public void setUpdate(Update update){
        this.update = update;
    }
    public Update getUpdate(){
        return this.update;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return this.status;
    }
    public void setNow(Now now){
        this.now = now;
    }
    public Now getNow(){
        return this.now;
    }
    public void setDaily_forecast(List<Daily_forecast> daily_forecast){
        this.daily_forecast = daily_forecast;
    }
    public List<Daily_forecast> getDaily_forecast(){
        return this.daily_forecast;
    }
    public void setLifestyle(List<Lifestyle> lifestyle){
        this.lifestyle = lifestyle;
    }
    public List<Lifestyle> getLifestyle(){
        return this.lifestyle;
    }
    public void setSunrise_sunset(List<Sunrise_sunset> sunrise_sunset){
        this.sunrise_sunset = sunrise_sunset;
    }
    public List<Sunrise_sunset> getSunrise_sunset(){
        return this.sunrise_sunset;
    }
}
