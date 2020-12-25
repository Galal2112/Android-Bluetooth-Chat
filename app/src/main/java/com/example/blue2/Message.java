package com.example.blue2;

public class Message {
    private boolean isMine;
    private String text;

    public Message(String text, boolean isMine) {
        this.text = text;
        this.isMine = isMine;
    }

    public String getText() {
        return text;
    }

    public boolean isMine() {
        return isMine;
    }
}
