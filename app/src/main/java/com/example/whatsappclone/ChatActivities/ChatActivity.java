package com.example.whatsappclone.ChatActivities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapters.MessagesAdapter;
import com.example.whatsappclone.generalClasses.SharedPreference;
import com.example.whatsappclone.models.Message;
import com.example.whatsappclone.models.User;
import com.example.whatsappclone.viewmodels.ChatActivityViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    String friendImageUrl;
    TextView friendNameTxtView;
    TextView statusTxtView;
    ImageView friendImageView;
    EditText messageEdtTxt;
    LinearLayout requestLinearLayout;
    FloatingActionButton sendBtn;
    Button acceptBtn, declineBtn;
    FirebaseDatabase database;
    DatabaseReference myRef, contactRef, requestsRef;
    SharedPreference sharedPreference;
    String friendUid;
    String friendUidInContacts;
    boolean isNewRequest;
    List<Message> allMessages, notificationMessages;
    ChatActivityViewModel chatActivityViewModel;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private User friendData;
    private RecyclerView recyclerView;
    private MessagesAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initObjects();
        receiveData();
        initView();
        Query applesQuery = myRef.child("Users").orderByChild("email").equalTo(friendData.getEmail());
        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                    friendUid = appleSnapshot.getKey();
                    Query applesQuery = requestsRef.orderByChild("messageContent");
                    applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                isNewRequest = true;
                            }
                            clickEvents();
                            setView();
                            checkFriendStatus();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("Database delete", "onCancelled", databaseError.toException());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Database delete", "onCancelled", databaseError.toException());
            }
        });
        initRecyclerView();
        chatActivityViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(ChatActivityViewModel.class); // this is the instance of AndroidViewModel

        chatActivityViewModel.getChats().observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messageList) {
                mAdapter.setList(messageList);
            }
        });
        setView();
    }

    void initRecyclerView() {
        recyclerView = findViewById(R.id.my_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        // specify an adapter (see also next example)
        // put you list on the constructor
        mAdapter = new MessagesAdapter(this);
        recyclerView.setAdapter(mAdapter);
    }

    void initView() {
        recyclerView = findViewById(R.id.my_recycler_view);
        friendNameTxtView = findViewById(R.id.Friend_name);
        friendImageView = findViewById(R.id.friend_image_view);
        statusTxtView = findViewById(R.id.status);
        messageEdtTxt = findViewById(R.id.message_edt_txt);
        sendBtn = findViewById(R.id.send_btn);
        requestLinearLayout = findViewById(R.id.request_linear_layout);
        acceptBtn = findViewById(R.id.accept_btn);
        declineBtn = findViewById(R.id.decline_btn);
    }

    private void clickEvents() {
        final String messageKey = myRef.push().getKey();
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        /******************************************************************************************/
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestsRef = myRef.child("Requests").child(firebaseUser.getUid())
                        .child(friendUid);
                requestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Message message = child.getValue(Message.class);
                            message.setMessageFromMe(false);
                            contactRef.child(friendUid).child(messageKey).setValue(message)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                        }
                                    });
                            message.setMessageFromMe(true);
                            myRef.child("Contacts").child(friendUid).child(firebaseUser.getUid())
                                    .child(messageKey).setValue(message)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Query applesQuery = requestsRef.orderByChild("messageContent");
                                            applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                                                        appleSnapshot.getRef().removeValue();
                                                    }
                                                    requestLinearLayout.setVisibility(View.GONE);
                                                    messageEdtTxt.setEnabled(true);
                                                    sendBtn.setEnabled(true);
                                                    getMessagesFromDatabase(friendUid);
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    Log.e("Database delete", "onCancelled", databaseError.toException());
                                                }
                                            });
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
        /*******************************************************************************************/
        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query applesQuery = requestsRef.orderByChild("senderName").equalTo(friendData.getUsername());
                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("Database delete", "onCancelled", databaseError.toException());
                    }
                });
            }
        });
    }

    void initObjects() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        contactRef = myRef.child("Contacts").child(firebaseUser.getUid());
        requestsRef = myRef.child("Requests").child(firebaseUser.getUid());
        friendData = new User();
        sharedPreference = new SharedPreference(this);
        friendUidInContacts = "";
        friendUid = "";
        isNewRequest = false;
    }

    void receiveData() {
        friendData = getIntent().getParcelableExtra("User");
    }

    void setView() {
        if (isNewRequest) {
            requestLinearLayout.setVisibility(View.VISIBLE);
            messageEdtTxt.setEnabled(false);
            sendBtn.setEnabled(false);
        }
        friendNameTxtView.setText(friendData.getUsername());
        statusTxtView.setText(friendData.getAbout());
        friendImageUrl = friendData.getImageUrl();
        if (!friendImageUrl.trim().equals(""))
            Picasso.get().load(friendImageUrl).placeholder(R.drawable.default_profile_image)
                    .error(R.drawable.default_profile_image).into(friendImageView);
        else
            friendImageView.setImageResource(R.drawable.default_profile_image);

        getMessagesFromDatabase(friendUid);
        if (isNewRequest)
            getMessagesFromRequests(friendUid);

    }

    void sendMessage() {
        String messageContent = messageEdtTxt.getText().toString();
        if (!messageContent.trim().isEmpty()) {
            Log.e("messageContnet", messageContent);
            Message message = new Message();
            message.setMessageContent(messageContent);
            message.setSenderName(sharedPreference.loadData().getUsername());
            message.setSenderID(firebaseUser.getUid());
            saveMessagesInDatabase(message);
            messageEdtTxt.setText("");
        }
    }

    void getMessagesFromDatabase(final String friendUid) {
        /*myRef.child("Contacts").child(firebaseUser.getUid())
                .child(friendUid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                allMessages=new ArrayList<>();
                Message newMessage = dataSnapshot.getValue(Message.class);
                allMessages.add(newMessage);
                displayMessages();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
        myRef.child("Contacts").child(firebaseUser.getUid())
                .child(friendUid).addValueEventListener(new ValueEventListener() {
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

    private void getMessagesFromRequests(final String friendUid) {
        requestsRef = myRef.child("Requests").child(friendUid)
                .child(firebaseUser.getUid());
        requestsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                allMessages.clear();
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

        if (isNewRequest) {
            requestsRef = myRef.child("Requests").child(firebaseUser.getUid())
                    .child(friendUid);
            requestsRef.addValueEventListener(new ValueEventListener() {
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
    }

    private void displayMessages() {
        mAdapter.setList(allMessages);
        chatActivityViewModel.insertAllMessages(allMessages);
        if (mAdapter.getItemCount() != 0)
            recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    void checkFriendStatus() {
        //Check if my friend is online
        myRef.child("Users").child(friendUid).child("online").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Boolean isFriendOnline = dataSnapshot.getValue(Boolean.class);
                if (isFriendOnline) {
                    statusTxtView.setText("Online");
                } else {
                    statusTxtView.setText("Offline");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void saveMessagesInDatabase(final Message message) {
        final String messageKey = myRef.push().getKey();
        //check if the friend in the user's contacts
        Query contacts = myRef.child("Contacts").child(firebaseUser.getUid()).orderByKey().equalTo(friendUid);
        contacts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userUid : dataSnapshot.getChildren()) {
                    friendUidInContacts = userUid.getKey();
                }
                // if not save the messages in the requests
                if (friendUidInContacts.trim().equals("")) {
                    message.setMessageFromMe(true);
                    myRef.child("Requests").child(friendUid).child(firebaseUser.getUid())
                            .child(messageKey).setValue(message)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    getMessagesFromRequests(friendUid);
                                }
                            });
                    // if yes save it in the contacts
                } else {
                    message.setMessageFromMe(true);
                    contactRef.child(friendUid).child(messageKey).setValue(message)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    getMessagesFromDatabase(friendUid);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                    message.setMessageFromMe(false);
                    myRef.child("Contacts").child(friendUid).child(firebaseUser.getUid()).child(messageKey)
                            .setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            getMessagesFromDatabase(friendUid);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Check if my friend is online
        if (statusTxtView.getText().equals("Offline"))
            sendDataToNotification(message);
    }

    void sendDataToNotification(Message message) {
        final String messageKey = myRef.push().getKey();
        myRef.child("Notifications").child(friendUid).child(messageKey)
                .setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });
    }
}

