package com.example.whatsappclone.generalClasses;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.whatsappclone.models.User;

public class SharedPreference {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public SharedPreference(Context context) {
        sharedPreferences = context.getSharedPreferences("myRef", Context.MODE_PRIVATE);
    }

    //This method is to save data into the sharedPreferences
    public void saveData(User user) {
        editor = sharedPreferences.edit();
        editor.putString("username", user.getUsername());//this method to save the data
        editor.putString("email", user.getEmail());
        editor.putString("about", user.getAbout());
        editor.putString("phone", user.getPhone());
        editor.putString("imageUrl", user.getImageUrl());
        editor.putBoolean("online" , user.isOnline());

        editor.commit();
    }

    //this method is to get the data from the sharedPreferences
    public User loadData() {
        User user = new User();
        user.setUsername(sharedPreferences.getString("username", "unknown"));//this method is to retrieve the data again
        user.setEmail(sharedPreferences.getString("email", "unknown"));
        user.setAbout(sharedPreferences.getString("about", "unknown"));
        user.setPhone(sharedPreferences.getString("phone", "unknown"));
        user.setImageUrl(sharedPreferences.getString("imageUrl",""));
        user.setOnline(sharedPreferences.getBoolean("online", false));

        return user;
    }
}


