package com.example.whatsappclone.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    String username;
    String email;
    String phone;
    String about;
    String imageUrl;

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
