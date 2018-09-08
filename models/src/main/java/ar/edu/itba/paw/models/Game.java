package ar.edu.itba.paw.models;

import org.joda.time.DateTime;

public class Game {
    private Team team1;
    private Team team2;
    private Place place;
    private DateTime startTime;
    private DateTime finishTime;
    private String result;
    private boolean isCompetitive;

    public Game(Team team1, Team team2, Place place, DateTime startTime,
                DateTime finishTime, boolean isCompetitive) {
        this.team1 = team1;
        this.team2 = team2;
        this.place = place;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.isCompetitive = isCompetitive;
        this.result = null;
    }

    public Team getTeam1() {
        return team1;
    }

    public Team getTeam2() {
        return team2;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public DateTime getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(DateTime finishTime) {
        this.finishTime = finishTime;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public boolean isCompetitive() {
        return isCompetitive;
    }
}
