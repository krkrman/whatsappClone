package com.example.whatsappclone.databases.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.whatsappclone.models.Message;
import com.example.whatsappclone.models.User;

import java.util.List;

@Dao
public interface MessageDao {
    @Insert
    void insert(Message message); //Here pass your the model of one raw
    @Update
    void update(Message message);
    @Delete
    void delete(Message message);
    @Query("DELETE FROM message_table")
    void deleteAllNotes();
    @Query("SELECT * FROM message_table")
    List<Message> getAllUsers();
}
