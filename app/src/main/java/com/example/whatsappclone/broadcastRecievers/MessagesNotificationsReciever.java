package com.example.whatsappclone.broadcastRecievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.RemoteInput;

import com.example.whatsappclone.MainActivity;
import com.example.whatsappclone.models.Message;
import com.example.whatsappclone.services.MessagesNotificationService;

public class MessagesNotificationsReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            CharSequence replyText = remoteInput.getCharSequence("key_text_reply");
            Message answer = new Message(replyText.toString() , "Ahmed" , true);
            MessagesNotificationService.allMessages.add(answer);
            MessagesNotificationService.sendChannel1Notification(context);
        }

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            MessagesNotificationService.sendChannel1Notification(context);
        }
    }
}
