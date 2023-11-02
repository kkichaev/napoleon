package com.grsoft.napoleon.main;

public class RefreshData {
    public boolean refreshing = false;
    public String error = "";
    public int traffic = 0;

    public RefreshData() {}

    public RefreshData(boolean refreshing) {
        this.refreshing = refreshing;
    }

    public RefreshData(String error) {
        refreshing = false;
        this.error = error;
    }

    public RefreshData(int traffic) {
        this.traffic = traffic;
    }
}
