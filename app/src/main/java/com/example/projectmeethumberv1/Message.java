package com.example.projectmeethumberv1;

public class Message {
    String userName;
    String textMessage;
    private long messageTime;
    Long groupId;

    public Message() {
    }

    public Message(String userName, String textMessage, long messageTime, Long groupId) {
        this.userName = userName;
        this.textMessage = textMessage;
        this.messageTime = messageTime;
        this.groupId = groupId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
}
