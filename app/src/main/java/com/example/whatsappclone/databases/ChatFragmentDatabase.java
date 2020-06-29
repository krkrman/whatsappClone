package com.example.whatsappclone.databases;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.whatsappclone.databases.room.dao.MessageDao;
import com.example.whatsappclone.databases.room.dao.UserDao;
import com.example.whatsappclone.models.Message;
import com.example.whatsappclone.models.User;

@Database(entities = {User.class , Message.class}, version = 4) //Here pass the tables like {Note.class , Person.class}
public abstract class ChatFragmentDatabase extends RoomDatabase {  // it is abstract class because we will use abstract methods
    private static ChatFragmentDatabase instance;
    public abstract UserDao userDao(); // it is abstract because we will not write body for the method
    public abstract MessageDao messageDao();

    // singlton : the next is to prevent the system from copying the database a lot if there are a lot of threads want to access it
    public static synchronized ChatFragmentDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    ChatFragmentDatabase.class, "chat_fragment_database")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        // this overwritten function will excute one time when the app is installed for the first time
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
        // this function will be excuted everytime you open the app
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }
    };

    // Async task to put initial data and we use Async task in order not to block the UI
    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private UserDao userDao;
        private MessageDao messageDao;
        private PopulateDbAsyncTask(ChatFragmentDatabase db) {
            userDao = db.userDao();
            messageDao = db.messageDao();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}