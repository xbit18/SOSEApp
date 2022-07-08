package com.example.soseapp;

public class CompleteMatch extends Match{
    private double localTeamQuote;
    private double visitorTeamQuote;
    private double tieQuote;
    private String coordinates;
    private String city;
    private Double temperature;
    private String weather;

    public double getLocalTeamQuote() {
        return localTeamQuote;
    }

    public void setLocalTeamQuote(double localTeamQuote) {
        this.localTeamQuote = localTeamQuote;
    }

    public double getVisitorTeamQuote() {
        return visitorTeamQuote;
    }

    public void setVisitorTeamQuote(double visitorTeamQuote) {
        this.visitorTeamQuote = visitorTeamQuote;
    }

    public double getTieQuote() {
        return tieQuote;
    }

    public void setTieQuote(double tieQuote) {
        this.tieQuote = tieQuote;
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

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public CompleteMatch(Team localTeam, Team visitorTeam, int localTeamScore, int visitorTeamScore, double localTeamQuote, double visitorTeamQuote, double tieQuote, String coordinates, String city, Double temperature, String weather) {
        super(localTeam, visitorTeam, localTeamScore, visitorTeamScore);
        this.localTeamQuote = localTeamQuote;
        this.visitorTeamQuote = visitorTeamQuote;
        this.tieQuote = tieQuote;
        this.coordinates = coordinates;
        this.city = city;
        this.temperature = temperature;
        this.weather = weather;
    }
}
