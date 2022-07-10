package com.example.soseapp;

public class Weather {
    private String weather;
    private Double temperature;
    private String city;

    public Weather(String weather, Double temperature, String city) {
        this.weather = weather;
        this.temperature = temperature;
        this.city = city;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
