package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.whatsappclone.ChatActivities.ChatActivity;
import com.example.whatsappclone.Settings.ProfileActivity;
import com.example.whatsappclone.fragments.RequestsFragment;
import com.example.whatsappclone.generalClasses.SharedPreference;
import com.example.whatsappclone.models.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {

    private DatabaseReference userRef;
    private RecyclerView recyclerView;
    SharedPreference sharedPreference;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        initObjects();
        initRecyclerView();
        init_toolbar();
    }

    void initObjects(){
        sharedPreference = new SharedPreference(this);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    void initRecyclerView() {
        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onStart() {
        super.onStart();
        initFirebaseAdapter();
    }

    public static class FindFriendViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userStatus;
        CircleImageView profileImage;
        LinearLayout wholeItem;

        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            wholeItem = itemView.findViewById(R.id.group_item_linear_layout);
            userName = itemView.findViewById(R.id.name_txt_view);
            userStatus = itemView.findViewById(R.id.last_message_txt_view);
            profileImage = itemView.findViewById(R.id.item_pic);
        }
    }

    void init_toolbar() {
        toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//to put arrow back
        getSupportActionBar().setTitle("SettingsActivity");
    }

    void initFirebaseAdapter() {
        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(userRef, User.class)
                        .build();

        FirebaseRecyclerAdapter<User,FindFriendViewHolder> adapter = new FirebaseRecyclerAdapter<User, FindFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull final User model) {
                if (!(model.getEmail() == sharedPreference.loadData().getEmail() &&
                        model.getPhone() == sharedPreference.loadData().getPhone() &&
                        model.getAbout() == sharedPreference.loadData().getAbout())) {
                    holder.userName.setText(model.getUsername());
                    holder.userStatus.setText(model.getAbout().trim());
                    //if (!model.getImageUrl().equals(""))
                     //   Picasso.get().load(model.getImageUrl()).placeholder(R.drawable.default_profile_image).into(holder.profileImage);
                    holder.profileImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                            intent.putExtra("isItMe", false);
                            intent.putExtra("User", model);
                            startActivity(intent);
                        }
                    });

                    holder.wholeItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                            intent.putExtra("User", model);
                            startActivity(intent);
                        }
                    });
                }else {
                    // this is to hide the user itself
                    holder.wholeItem.setVisibility(View.GONE);
                }
            }

            @NonNull
            @Override
            public  FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_item, viewGroup, false);
                FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);
                return viewHolder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

}
