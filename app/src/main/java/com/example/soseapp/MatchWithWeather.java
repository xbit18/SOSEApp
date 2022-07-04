package com.example.soseapp;

public class MatchWithWeather extends Match{
private String coordinates;
private String city;
private float temperature;
    public MatchWithWeather(Team localTeam, Team visitorTeam, int localTeamScore, int visitorTeamScore, String coordinates, String city, float temperature) {
        super(localTeam, visitorTeam, localTeamScore, visitorTeamScore);
        this.city = city;
        this.temperature = temperature;
        this.coordinates = coordinates;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }
}
