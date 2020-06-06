package com.example.whatsappclone.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.whatsappclone.ChatActivities.ChatActivity;
import com.example.whatsappclone.R;
import com.example.whatsappclone.Settings.ProfileActivity;
import com.example.whatsappclone.generalClasses.SharedPreference;
import com.example.whatsappclone.models.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatsFragment extends Fragment {

    public ChatsFragment() {
        // Required empty public constructor
    }

    SharedPreference sharedPreference;
    // TODO: Rename and change types of parameters
    private RequestsFragment.OnFragmentInteractionListener mListener;
    private DatabaseReference userRef;
    private RecyclerView recyclerView;
    View chatFragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        chatFragmentView = inflater.inflate(R.layout.fragment_chats, container, false);
        initRecyclerView();
        sharedPreference = new SharedPreference(getContext());
        return chatFragmentView;
    }

    void initRecyclerView() {
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        recyclerView = chatFragmentView.findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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

    void initFirebaseAdapter() {
        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(userRef, User.class)
                        .build();

        FirebaseRecyclerAdapter<User, FindFriendViewHolder> adapter = new FirebaseRecyclerAdapter<User, FindFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull final User model) {
                if (!(model.getEmail() == sharedPreference.loadData().getEmail() &&
                        model.getPhone() == sharedPreference.loadData().getPhone() &&
                        model.getAbout() == sharedPreference.loadData().getAbout())) {
                    holder.userName.setText(model.getUsername());
                    holder.userStatus.setText(model.getAbout().trim());
                    if (!model.getImageUrl().equals(""))
                        Picasso.get().load(model.getImageUrl()).placeholder(R.drawable.default_profile_image).into(holder.profileImage);
                    holder.profileImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), ProfileActivity.class);
                            intent.putExtra("isItMe", false);
                            intent.putExtra("User", model);
                            startActivity(intent);
                        }
                    });

                    holder.wholeItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), ChatActivity.class);
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
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_item, viewGroup, false);
                FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);
                return viewHolder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

}
