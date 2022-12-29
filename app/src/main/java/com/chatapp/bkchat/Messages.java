package com.chatapp.bkchat;

public class Messages {
    private String date;
    private String from;
    private String message;
    private String messageID;
    private String name;
    private String time;
    private String to;
    private String type;

    public Messages(String date, String from, String message, String messageID, String name, String time, String to, String type) {
        this.date = date;
        this.from = from;
        this.message = message;
        this.messageID = messageID;
        this.name = name;
        this.time = time;
        this.to = to;
        this.type = type;
    }

    public Messages() {
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
