package com.pawelwuuu;

public class Message {
    String timestamp, content, sender;

    public Message(String content, String sender) {
        this.content = content;
        this.sender = sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }

    public String getSender() {
        return sender;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "com.pawelwuuu.Message{" +
                "timestamp='" + timestamp + '\'' +
                ", content='" + content + '\'' +
                ", sender='" + sender + '\'' +
                '}';
    }
}
