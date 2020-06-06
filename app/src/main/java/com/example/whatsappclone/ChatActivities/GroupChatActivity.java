package com.example.whatsappclone.ChatActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.generalClasses.SharedPreference;
import com.example.whatsappclone.models.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GroupChatActivity extends AppCompatActivity {
    String groupName;
    String participatedPeople;
    int groupImage;
    TextView groupNameTxtView;
    TextView participatedPeopleTxtView;
    TextView messagesTxtView;
    ImageView groupImageView;
    EditText messageEdtTxt;
    FloatingActionButton sendBtn;
    ScrollView scrollView;
    FirebaseDatabase database;
    DatabaseReference myRef, groupMessages, groupMessageKeyRef;
    SharedPreference sharedPreference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        initObjects();
        initView();
        recievingData();
        updateUi();
        clickEvents();
        getMessagesFromDatabase();
    }

    private void clickEvents() {
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    void initObjects() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        sharedPreference = new SharedPreference(this);
    }

    private void initView() {
        groupNameTxtView = findViewById(R.id.Friend_name);
        messagesTxtView = findViewById(R.id.messages_txt_view);
        groupImageView = findViewById(R.id.friend_image_view);
        participatedPeopleTxtView = findViewById(R.id.status);
        messageEdtTxt = findViewById(R.id.message_edt_txt);
        sendBtn = findViewById(R.id.send_btn);
        scrollView = findViewById(R.id.scroll_view);
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    void recievingData() {
        groupName = getIntent().getStringExtra("groupName");
        groupImage = getIntent().getIntExtra("groupImage", R.drawable.group_image);
        participatedPeople = getIntent().getStringExtra("participatedPeople");
    }

    void updateUi() {
        groupNameTxtView.setText(groupName);
        groupImageView.setImageResource(groupImage);
        participatedPeopleTxtView.setText(participatedPeople);
    }

    void sendMessage() {
        String messageContent = messageEdtTxt.getText().toString();
        String messageKey = myRef.push().getKey();
        if (!messageContent.trim().isEmpty()) {
            Log.e("messageContent", messageContent);
            Message message = new Message();
            message.setMessageContent(messageContent);
            message.setSenderName(sharedPreference.loadData().getUsername());
            groupMessageKeyRef = myRef.child("Groups").child(groupName).child("Messages").child(messageKey);
            groupMessageKeyRef.setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }

            });
            messageEdtTxt.setText("");
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }

    void getMessagesFromDatabase() {
        groupMessages = myRef.child("Groups").child(groupName).child("Messages");
        groupMessages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                messagesTxtView.setText("");
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Message allMessages = child.getValue(Message.class);
                    displayMessages(allMessages);
                }
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        
    }

    private void displayMessages(Message allMessages) {
        messagesTxtView.append(allMessages.getSenderName() + " :" +"\n" + allMessages.getMessageContent() + "\n"
                +allMessages.getTime() + "  "+ allMessages.getDate() + "\n\n\n");
    }
}
