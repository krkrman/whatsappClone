package com.example.whatsappclone.models;

public class GroupModelItem {

    private String groupName;
    private String lastMessage;
    private String participatedPeople;
    private int image;

    public GroupModelItem(String groupName, String lastMessage, int image , String participatedPeople) {
        this.groupName = groupName;
        this.lastMessage = lastMessage;
        this.participatedPeople = participatedPeople;
        this.image = image;
    }

    public GroupModelItem() {
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getParticipatedPeople() {
        return participatedPeople;
    }

    public void setParticipatedPeople(String participatedPeople) {
        this.participatedPeople = participatedPeople;
    }

}
