package com.example.recyclar;

public class Pomodoro {
    private final int CountDown;
    private final String matters;

    public Pomodoro(String matters,int countDown) {
        CountDown = countDown;
        this.matters = matters;
    }

    public int getTime() {
        return CountDown;
    }

    public String getMatters() {
        return matters;
    }
}
