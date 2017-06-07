package com.gorrilaport.mysteryshoptools.ui.timer;

public class Period {
    public int minutes = 0;
    public int seconds = 0;
    public int tenOfSec = 0;

    Period() {
    }


    Period(int minutes, int seconds, int tenOfSec) {
        this.minutes = minutes;
        this.seconds = seconds;
        this.tenOfSec = tenOfSec;
    }
}