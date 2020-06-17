package com.example.whatsappclone.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import com.example.whatsappclone.MainActivity;
import com.example.whatsappclone.R;
import com.example.whatsappclone.broadcastRecievers.MessagesNotificationsReciever;
import com.example.whatsappclone.models.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.whatsappclone.notification.App.CHANNEL_ID;

public class MessagesNotificationService extends JobIntentService {
    private static final String TAG = "Service";
    public static ArrayList<Message> allMessages;
    boolean isItFirstTime;
    DatabaseReference myRef;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, MessagesNotificationService.class, 123, work);
    }

    public static void sendChannel1Notification(Context context) {
        Intent activityIntent = new Intent(context, MainActivity.class); //when pressing on notification go to MainActivity
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, activityIntent, 0);

        RemoteInput remoteInput = new RemoteInput.Builder("key_text_reply") //result key is const
                .setLabel("Your answer...") // this is written when you press send
                .build();
        Intent replyIntent;
        PendingIntent replyPendingIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            replyIntent = new Intent(context, MessagesNotificationsReciever.class);
            replyPendingIntent = PendingIntent.getBroadcast(context,    //to go to broadcast and send message
                    0, replyIntent, 0);
        } else {
            //start chat activity instead (PendingIntent.getActivity)
            //cancel notification with notificationManagerCompat.cancel(id)
        }
        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                R.drawable.ic_baseline_reply_24,
                "Reply",
                replyPendingIntent
        ).addRemoteInput(remoteInput).build();
        NotificationCompat.MessagingStyle messagingStyle =
                new NotificationCompat.MessagingStyle("Me");
        messagingStyle.setConversationTitle("Group Chat");

        for (Message chatMessage : allMessages) {
            NotificationCompat.MessagingStyle.Message notificationMessage =
                    new NotificationCompat.MessagingStyle.Message(
                            chatMessage.getMessageContent(),
                            //Long.parseLong(chatMessage.getTime()),
                            156,
                            chatMessage.getSenderName());
            messagingStyle.addMessage(notificationMessage);
        }
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_whatsapp)
                .setStyle(messagingStyle)
                .addAction(replyAction)
                .setColor(Color.BLUE)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, notification);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myRef = FirebaseDatabase.getInstance().getReference();
        allMessages = new ArrayList<>();
        isItFirstTime = true;
        Log.d(TAG, "onCreate");
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d(TAG, "onHandleWork");
        String uid = intent.getStringExtra("myUid");
        myRef.child("Notifications").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allMessages = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Message newMessage = child.getValue(Message.class);
                    allMessages.add(newMessage);
                }
                if (!isItFirstTime) {
                    Toast.makeText(MessagesNotificationService.this, "Entered", Toast.LENGTH_SHORT).show();
                    sendChannel1Notification(MessagesNotificationService.this);
                }
                isItFirstTime = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public boolean onStopCurrentWork() {
        Log.d(TAG, "onStopCurrentWork");
        return super.onStopCurrentWork();
    }


}
