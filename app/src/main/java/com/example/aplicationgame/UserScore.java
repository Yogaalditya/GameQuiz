package com.example.aplicationgame;

public class UserScore {
    private String nickname;
    private int points;
    private long time;

    public UserScore(String nickname, int points, long time) {
        this.nickname = nickname;
        this.points = points;
        this.time = time;
    }

    public String getNickname() {
        return nickname;
    }

    public int getPoints() {
        return points;
    }

    public long getTime() {
        return time;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setTime(long time) {
        this.time = time;
    }
}