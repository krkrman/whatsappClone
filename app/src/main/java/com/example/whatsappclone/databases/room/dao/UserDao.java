package com.example.whatsappclone.databases.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.whatsappclone.models.User;
import java.util.ArrayList;
import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insert(User user); //Here pass your the model of one raw
    @Update
    void update(User user);
    @Delete
    void delete(User user);
    @Query("DELETE FROM user_table")
    void deleteAllNotes();
    @Query("SELECT * FROM user_table")
    List<User> getAllUsers();
}
