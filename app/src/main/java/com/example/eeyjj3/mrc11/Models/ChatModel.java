package com.example.eeyjj3.mrc11.Models;

/**
 * Created by eeyjj3 on 28/04/2018.
 */

public class ChatModel {
    public String message;//chat message
    public boolean isSend;//if the respond is received
    private String time;//the chat time

    public ChatModel(String message, boolean isSend,String time) {
        this.message = message;
        this.isSend = isSend;
        this.time = time;
    }
    public ChatModel(String message, boolean isSend) {
        this.message = message;
        this.isSend = isSend;
    }

    public ChatModel() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}