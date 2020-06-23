package com.example.whatsappclone.repos;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.whatsappclone.databases.ChatFragmentDatabase;
import com.example.whatsappclone.databases.room.dao.UserDao;
import com.example.whatsappclone.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatFragmentRepository {
    MutableLiveData<List<User>> allChatsMutableLiveData;
    LiveData<List<User>> roomData;
    FirebaseUser firebaseUser;
    DatabaseReference myRef;
    List<User> allChatsUsers;
    private UserDao userDao;

    public ChatFragmentRepository(Application application) {
        allChatsMutableLiveData = new MutableLiveData<>();
        allChatsUsers = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        ChatFragmentDatabase database = ChatFragmentDatabase.getInstance(application);
        userDao = database.userDao();
        myRef = FirebaseDatabase.getInstance().getReference();
        allChatsMutableLiveData.setValue(userDao.getAllUsers());
    }

    public void getAllUsersFromFirebase() {
        myRef.child("Contacts").child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                            myRef.child("Users").child(chatSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    allChatsUsers.clear();
                                    allChatsUsers.add(dataSnapshot.getValue(User.class));
                                    allChatsMutableLiveData.setValue(allChatsUsers);
                                    insertAllChats(allChatsUsers);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public void insert(User user) {
        new InsertUserAsyncTask(userDao).execute(user);
    }

    public void update(User user) {
        new UpdateUserAsyncTask(userDao).execute(user);
    }

    public void delete(User user) {
        new DeleteUserAsyncTask(userDao).execute(user);
    }

    public void deleteAllChats() {
        new DeleteAllUsersAsyncTask(userDao).execute();
    }

    public void insertAllChats(List<User> allUsers) {
        deleteAllChats();
        for (int i = 0; i < allUsers.size(); i++) {
            insert(allUsers.get(i));
        }
    }

    public MutableLiveData<List<User>> getLiveData() {
        return allChatsMutableLiveData;
    }

    // Async tasks to make in a new thread in order not block the UI
    private static class InsertUserAsyncTask extends AsyncTask<User, Void, Void> {
        private UserDao userDao;

        private InsertUserAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(User... users) {
            userDao.insert(users[0]);
            return null;
        }
    }

    private static class UpdateUserAsyncTask extends AsyncTask<User, Void, Void> {
        private UserDao userDao;

        private UpdateUserAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(User... users) {
            userDao.update(users[0]);
            return null;
        }
    }

    private static class DeleteUserAsyncTask extends AsyncTask<User, Void, Void> {
        private UserDao userDao;

        private DeleteUserAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(User... users) {
            userDao.delete(users[0]);
            return null;
        }
    }

    private static class DeleteAllUsersAsyncTask extends AsyncTask<Void, Void, Void> {
        private UserDao userDao;

        private DeleteAllUsersAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            userDao.deleteAllNotes();
            return null;
        }
    }
}
