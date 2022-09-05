package com.pawelwuuu;

/**
 * Message that can be sent or received by a client. Contains information about sender (nickname), content of the message
 * and information that indicates if the message have information for server or client backend view only (password).
 */
public class Message {
    private String content, sender;
    private boolean containingServerInformation;

    /**
     * Constructs a message object.
     * @param content string containing content of the message.
     * @param sender string containing nickname of the sender.
     * @param containingServerInformation boolean indicating if the message is for backend view only.
     */
    public Message(String content, String sender, boolean containingServerInformation) {
        this.content = content;
        this.sender = sender;
        this.containingServerInformation = containingServerInformation;
    }

    /**
     * Constructs a message object with backend view information set to false.
     * @param content string containing content of the message.
     * @param sender string containing nickname of the sender.
     */
    public Message(String content, String sender) {
        this(content, sender, false);
    }

    public String getContent() {
        return content;
    }

    public String getSender() {
        return sender;
    }

    public boolean isContainingServerInformation() {
        return containingServerInformation;
    }

    @Override
    public String toString() {
        return "Message{" +
                "content='" + content + '\'' +
                ", sender='" + sender + '\'' +
                '}';
    }
}
