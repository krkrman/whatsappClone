package com.example.whatsappclone.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Calendar;
@Entity(tableName = "message_table") // if you did not write table name it will be the class name by default
public class Message {
    @PrimaryKey(autoGenerate = true) // every new row will have new id automatically
    public int id;
    public String date;
    public String time;
    String messageContent;
    String senderName;
    String senderID;
    boolean messageFromMe;
    boolean isMessageRead;


    public void setId(int id) {
        this.id = id;
    }

    public boolean isMessageRead() {
        return isMessageRead;
    }

    public void setMessageRead(boolean messageRead) {
        isMessageRead = messageRead;
    }

    public boolean isMessageFromMe() {
        return messageFromMe;
    }

    public void setMessageFromMe(boolean messageFromMe) {
        this.messageFromMe = messageFromMe;
    }

    public Message(String messageContent, String senderName , boolean messageFromMe ) {
        Calendar calendarForDate = Calendar.getInstance();
        SimpleDateFormat simpleDateFormatForDate = new SimpleDateFormat("MMM dd, yyyy");
        date = simpleDateFormatForDate.format(calendarForDate.getTime());

        Calendar calendarForTime = Calendar.getInstance();
        SimpleDateFormat simpleDateFormatForTime = new SimpleDateFormat("hh:mm");
        time = simpleDateFormatForTime.format(calendarForTime.getTime());

        this.messageContent = messageContent;
        this.senderName = senderName;
        this.messageFromMe = messageFromMe;
    }

    public Message() {
        Calendar calendarForDate = Calendar.getInstance();
        SimpleDateFormat simpleDateFormatForDate = new SimpleDateFormat("MMM dd, yyyy");
        this.date = simpleDateFormatForDate.format(calendarForDate.getTime());

        Calendar calendarForTime = Calendar.getInstance();
        SimpleDateFormat simpleDateFormatForTime = new SimpleDateFormat("hh:mm");
        this.time = simpleDateFormatForTime.format(calendarForTime.getTime());
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getSenderID(){
        return senderID;
    }

    public void setSenderID(String senderID){
        this.senderID = senderID;
    }
}
