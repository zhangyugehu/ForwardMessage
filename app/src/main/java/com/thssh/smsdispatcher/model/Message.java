package com.thssh.smsdispatcher.model;

import static com.thssh.smsdispatcher.tools.Util.getPhoneNumber;

public class Message {

    private long timestamp;
    private String title;
    private String message;
    private String username;
    private String packageName;
    private String phone;

    public Message(long timestamp, String title, String message, String packageName) {
        this.timestamp = timestamp;
        this.title = title;
        this.message = message;
        this.packageName = packageName;
        this.phone = getPhoneNumber();
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
