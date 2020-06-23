package com.example.whatsappclone.repos;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.whatsappclone.databases.ChatFragmentDatabase;
import com.example.whatsappclone.databases.room.dao.MessageDao;
import com.example.whatsappclone.models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChatActivityRepository {
    private MessageDao messageDao;
    MutableLiveData<List<Message>> allMessagesMutableLiveData;
    LiveData<List<Message>> messageRoomData;
    FirebaseUser firebaseUser;
    DatabaseReference myRef;
    List<Message> allMessages;

    public ChatActivityRepository(Application application) {
        allMessagesMutableLiveData = new MutableLiveData<>();
        allMessages = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        ChatFragmentDatabase database = ChatFragmentDatabase.getInstance(application);
        messageDao = database.messageDao();
        myRef = FirebaseDatabase.getInstance().getReference();
        allMessagesMutableLiveData.setValue(messageDao.getAllUsers());
    }

    public void insert(Message message) {
        new ChatActivityRepository.InsertUserAsyncTask(messageDao).execute(message);
    }

    public void update(Message message) {
        new ChatActivityRepository.UpdateUserAsyncTask(messageDao).execute(message);
    }

    public void delete(Message message) {
        new ChatActivityRepository.DeleteUserAsyncTask(messageDao).execute(message);
    }

    public void deleteAllMessages(){
        new ChatActivityRepository.DeleteAllUsersAsyncTask(messageDao).execute();
    }

    public void insertAllChats(List<Message> allMessages) {
        deleteAllMessages();
        for (int i = 0; i<allMessages.size(); i++){
            insert(allMessages.get(i));
        }
    }

    // Async tasks to make in a new thread in order not block the UI
    private static class InsertUserAsyncTask extends AsyncTask<Message, Void, Void> {
        private MessageDao messageDao;
        private InsertUserAsyncTask(MessageDao messageDao) {
            this.messageDao = messageDao;
        }
        @Override
        protected Void doInBackground(Message... messages) {
            messageDao.insert(messages[0]);
            return null;
        }
    }
    private static class UpdateUserAsyncTask extends AsyncTask<Message, Void, Void> {
        private MessageDao messageDao;
        private UpdateUserAsyncTask(MessageDao messageDao) {
            this.messageDao = messageDao;
        }
        @Override
        protected Void doInBackground(Message... messages) {
            messageDao.update(messages[0]);
            return null;
        }
    }
    private static class DeleteUserAsyncTask extends AsyncTask<Message, Void, Void> {
        private MessageDao messageDao;
        private DeleteUserAsyncTask(MessageDao messageDao) {
            this.messageDao = messageDao;
        }
        @Override
        protected Void doInBackground(Message... messages) {
            messageDao.delete(messages[0]);
            return null;
        }
    }
    private static class DeleteAllUsersAsyncTask extends AsyncTask<Void, Void, Void> {
        private MessageDao messageDao;
        private DeleteAllUsersAsyncTask(MessageDao messageDao) {
            this.messageDao = messageDao;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            messageDao.deleteAllNotes();
            return null;
        }
    }

    public MutableLiveData<List<Message>> getLiveData() {
        return allMessagesMutableLiveData;
    }
}
