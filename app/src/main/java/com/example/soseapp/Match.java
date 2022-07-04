package com.example.soseapp;

public class Match {
    private Team localTeam;
    private Team visitorTeam;
    private int localTeamScore;
    private int visitorTeamScore;

    public Match(Team localTeam, Team visitorTeam, int localTeamScore, int visitorTeamScore) {
        this.localTeam = localTeam;
        this.visitorTeam = visitorTeam;
        this.localTeamScore = localTeamScore;
        this.visitorTeamScore = visitorTeamScore;
    }

    public Team getLocalTeam() {
        return localTeam;
    }

    public void setLocalTeam(Team localTeam) {
        this.localTeam = localTeam;
    }

    public Team getVisitorTeam() {
        return visitorTeam;
    }

    public void setVisitorTeam(Team visitorTeam) {
        this.visitorTeam = visitorTeam;
    }

    public int getLocalTeamScore() {
        return localTeamScore;
    }

    public void setLocalTeamScore(int localTeamScore) {
        this.localTeamScore = localTeamScore;
    }

    public int getVisitorTeamScore() {
        return visitorTeamScore;
    }

    public void setVisitorTeamScore(int visitorTeamScore) {
        this.visitorTeamScore = visitorTeamScore;
    }
}
