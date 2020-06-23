package com.example.whatsappclone.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.whatsappclone.models.Message;
import com.example.whatsappclone.models.User;
import com.example.whatsappclone.repos.ChatActivityRepository;

import java.util.List;

public class ChatActivityViewModel extends AndroidViewModel {
    private ChatActivityRepository repository;
    private MutableLiveData<List<Message>> allMessagesLiveData;

    public ChatActivityViewModel(@NonNull Application application) {
        super(application);
        allMessagesLiveData = new MutableLiveData<>();
        repository = new ChatActivityRepository(application);
        allMessagesLiveData = repository.getLiveData();
    }

    public MutableLiveData<List<Message>> getChats(){
        return allMessagesLiveData;
    }

    public void insertAllMessages(List<Message> allMessages){
        repository.insertAllChats(allMessages);
    }
    public void insert(Message message) {
        repository.insert(message);
    }
    public void update(Message message) {
        repository.update(message);
    }
    public void delete(Message message) {
        repository.delete(message);
    }
    public void deleteAllChats(){
        repository.deleteAllMessages();
    }
}
