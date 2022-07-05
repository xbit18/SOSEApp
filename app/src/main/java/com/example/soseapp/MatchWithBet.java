package com.example.soseapp;

public class MatchWithBet extends Match{
    private double localTeamQuote;
    private double visitorTeamQuote;
    private double tieQuote;
    private String coordinates;

    public MatchWithBet(Team localTeam, Team visitorTeam, int localTeamScore, int visitorTeamScore, double localTeamQuote, double visitorTeamQuote, double tieQuote, String coordinates) {
        super(localTeam, visitorTeam, localTeamScore, visitorTeamScore);
        this.localTeamQuote = localTeamQuote;
        this.visitorTeamQuote = visitorTeamQuote;
        this.tieQuote = tieQuote;
        this.coordinates=coordinates;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public double getLocalTeamQuote() {
        return localTeamQuote;
    }

    public void setLocalTeamQuote(int localTeamQuote) {
        this.localTeamQuote = localTeamQuote;
    }

    public double getVisitorTeamQuote() {
        return visitorTeamQuote;
    }

    public void setVisitorTeamQuote(int visitorTeamQuote) {
        this.visitorTeamQuote = visitorTeamQuote;
    }

    public double getTieQuote() {
        return tieQuote;
    }

    public void setTieQuote(double tieQuote) {
        this.tieQuote = tieQuote;
    }
}
