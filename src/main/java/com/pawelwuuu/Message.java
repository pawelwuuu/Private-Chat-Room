package com.pawelwuuu;

/**
 * Message that can be sent or received by a client. Contains information about sender (nickname) and content of the message.
 */
public class Message {
    private String content, sender;

    /**
     * Constructs a message object.
     * @param content string containing content of the message.
     * @param sender string containing nickname of the sender.
     */
    public Message(String content, String sender) {
        this.content = content;
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public String getSender() {
        return sender;
    }

    @Override
    public String toString() {
        return "Message{" +
                "content='" + content + '\'' +
                ", sender='" + sender + '\'' +
                '}';
    }
}
