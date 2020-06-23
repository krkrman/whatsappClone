package com.example.whatsappclone.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table") // if you did not write table name it will be the class name by default
public class User implements Parcelable {
    public int getId() {
        return id;
    }

    @PrimaryKey(autoGenerate = true) // every new row will have new id automatically
    int id;
    String username;
    String email;
    String phone;
    String about;
    String imageUrl;
    boolean online;

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public User() {
        username = "unknown";
        email = "unknown";
        phone = "unknown";
        about = "unknown";
        imageUrl = "";
    }

    protected User(Parcel in) {
        username = in.readString();
        email = in.readString();
        phone = in.readString();
        about = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public void setId(int id){
        this.id = id;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String image) {
        this.about = image;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(about);
        dest.writeString(imageUrl);
    }
}
