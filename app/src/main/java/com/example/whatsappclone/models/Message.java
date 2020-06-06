package com.example.whatsappclone.models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Message {
    String date;
    String time;
    String messageContent;
    String senderName;

    public Message(String messageContent, String senderName) {
        Calendar calendarForDate = Calendar.getInstance();
        SimpleDateFormat simpleDateFormatForDate = new SimpleDateFormat("MMM dd, yyyy");
        date = simpleDateFormatForDate.format(calendarForDate.getTime());

        Calendar calendarForTime = Calendar.getInstance();
        SimpleDateFormat simpleDateFormatForTime = new SimpleDateFormat("hh:mm");
        time = simpleDateFormatForTime.format(calendarForTime.getTime());

        this.messageContent = messageContent;
        this.senderName = senderName;
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
}
