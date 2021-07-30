package com.thssh.smsdispatcher.model;

public class Report {
    public Report(String title, String message, long timestamp) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String title;
    public String message;
    public long timestamp;
}
