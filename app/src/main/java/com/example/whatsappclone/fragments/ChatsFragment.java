package com.example.whatsappclone.fragments;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsappclone.MainActivity;
import com.example.whatsappclone.viewmodels.ChatsViewModel;
import com.example.whatsappclone.R;
import com.example.whatsappclone.adapters.ChatListAdapter;
import com.example.whatsappclone.generalClasses.SharedPreference;
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

import static com.google.gson.reflect.TypeToken.get;


public class ChatsFragment extends Fragment {

    public ChatsFragment() {
    }
    public interface ChatsFragmentListener {
        void onInputASent(CharSequence input);
    }

    SharedPreference sharedPreference;
    // TODO: Rename and change types of parameters
    private RequestsFragment.OnFragmentInteractionListener mListener;
    private DatabaseReference userRef;
    View chatFragmentView;
    ChatsViewModel chatsViewModel;
    private RecyclerView recyclerView;
    public ChatListAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    DatabaseReference myRef;
    FirebaseUser firebaseUser;
    List<User> allChatsUsers;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        chatFragmentView = inflater.inflate(R.layout.fragment_chats, container, false);
        initRecyclerView();
        sharedPreference = new SharedPreference(getContext());
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference();
        allChatsUsers = new ArrayList<>();

        chatsViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(ChatsViewModel.class); // this is the instance of AndroidViewModel

        chatsViewModel.getChats().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                mAdapter.setList(users);
            }
        });

        getAllUsersFromFirebase();

        return chatFragmentView;
    }
    public void getAllUsersFromFirebase() {
        myRef.child("Contacts").child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        allChatsUsers.clear();
                        chatsViewModel.deleteAllChats();
                        for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                            myRef.child("Users").child(chatSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    allChatsUsers.add(dataSnapshot.getValue(User.class));
                                     mAdapter.setList(allChatsUsers);
                                     chatsViewModel.insert(dataSnapshot.getValue(User.class));
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

    void initRecyclerView(){
        recyclerView = chatFragmentView.findViewById(R.id.my_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        // put you list on the constructor
        mAdapter = new ChatListAdapter(getContext());
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        //initFirebaseAdapter();
    }


    public void setFilteredList(String searchedItems){
        mAdapter.getFilter().filter(searchedItems);
    }

    /*public static class FindFriendViewHolder extends RecyclerView.ViewHolder {
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
*/
}
