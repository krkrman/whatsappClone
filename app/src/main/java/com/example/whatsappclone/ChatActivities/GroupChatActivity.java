package com.example.whatsappclone.ChatActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapters.GroupListAdapter;
import com.example.whatsappclone.adapters.GroupMessagesAdapter;
import com.example.whatsappclone.adapters.MessagesAdapter;
import com.example.whatsappclone.generalClasses.SharedPreference;
import com.example.whatsappclone.models.GroupModelItem;
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

import java.util.ArrayList;

public class GroupChatActivity extends AppCompatActivity {
    String groupName;
    String participatedPeople;
    String myUID;
    int groupImage;
    TextView groupNameTxtView;
    TextView participatedPeopleTxtView;
    ImageView groupImageView;
    EditText messageEdtTxt;
    FloatingActionButton sendBtn;
    FirebaseDatabase database;
    FirebaseUser firebaseUser;
    DatabaseReference myRef, groupMessages, groupMessageKeyRef;
    SharedPreference sharedPreference;
    RecyclerView messagesRecyclerView;
    private ArrayList<Message> allMessages;
    private GroupMessagesAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        initObjects();
        initView();
        receivingData();
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
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        sharedPreference = new SharedPreference(this);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myUID = firebaseUser.getUid();
    }

    private void initView() {
        groupNameTxtView = findViewById(R.id.Friend_name);
        messagesRecyclerView = findViewById(R.id.my_recycler_view);
        groupImageView = findViewById(R.id.friend_image_view);
        participatedPeopleTxtView = findViewById(R.id.status);
        messageEdtTxt = findViewById(R.id.message_edt_txt);
        sendBtn = findViewById(R.id.send_btn);
        initRecyclerView();
    }

    void initRecyclerView(){
        messagesRecyclerView = findViewById(R.id.my_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        messagesRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        messagesRecyclerView.setLayoutManager(layoutManager);
        // specify an adapter (see also next example)
        // put you list on the constructor
        mAdapter = new GroupMessagesAdapter(this);
        messagesRecyclerView.setAdapter(mAdapter);
    }

    void receivingData() {
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
            message.setSenderID(myUID);
            groupMessageKeyRef = myRef.child("Groups").child(groupName).child("Messages").child(messageKey);
            groupMessageKeyRef.setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    getMessagesFromDatabase();
                }
            });
            messageEdtTxt.setText("");
        }
    }

    void getMessagesFromDatabase() {
        groupMessages = myRef.child("Groups").child(groupName).child("Messages");
        groupMessages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                allMessages = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Message newMessage = child.getValue(Message.class);
                    allMessages.add(newMessage);
                }
                displayMessages();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void displayMessages() {
        mAdapter.setList(allMessages);
        if (mAdapter.getItemCount() != 0)
            messagesRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }
}
