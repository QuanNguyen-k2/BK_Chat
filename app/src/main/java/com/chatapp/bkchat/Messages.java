package com.chatapp.bkchat;

public class Messages {
    private String from, type, message, name, to, messagesID,time, date;

    public Messages(){}

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

    public String getMessagesID() {
        return messagesID;
    }

    public void setMessagesID(String messagesID) {
        this.messagesID = messagesID;
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

    public Messages(String from, String type, String message, String name, String to, String messagesID, String time, String date) {
        this.from = from;
        this.type = type;
        this.message = message;
        this.name = name;
        this.to = to;
        this.messagesID = messagesID;
        this.time = time;
        this.date = date;
    }
}
