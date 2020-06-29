package com.example.whatsappclone.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.whatsappclone.repos.ChatFragmentRepository;
import com.example.whatsappclone.models.User;

import java.util.ArrayList;
import java.util.List;

public class ChatsViewModel extends AndroidViewModel {
    private ChatFragmentRepository repository;
    private MutableLiveData<List<User>> allUsersInContactsLiveData;

    public ChatsViewModel(@NonNull Application application) {
        super(application);
        allUsersInContactsLiveData = new MutableLiveData<>();
        repository = new ChatFragmentRepository(application);
        allUsersInContactsLiveData = repository.getLiveData();
    }

    public MutableLiveData<List<User>> getChats(){
        allUsersInContactsLiveData = repository.getLiveData();
        return allUsersInContactsLiveData;
    }

    public void insert(User user) {
        repository.insert(user);
    }
    public void update(User user) {
        repository.update(user);
    }
    public void delete(User user) {
        repository.delete(user);
    }
    public void deleteAllChats(){
        repository.deleteAllChats();
    }
}
