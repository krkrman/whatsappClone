package com.example.whatsappclone.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.ChatActivities.ChatActivity;
import com.example.whatsappclone.R;
import com.example.whatsappclone.Settings.ProfileActivity;
import com.example.whatsappclone.generalClasses.SharedPreference;
import com.example.whatsappclone.models.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RequestsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RequestsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    View groupFragmentView;
    SharedPreference sharedPreference;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private DatabaseReference contactsRef , userRef , requestsRef;
    private RecyclerView recyclerView;
    private FirebaseUser firebaseUser;

    public RequestsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RequestsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RequestsFragment newInstance(String param1, String param2) {
        RequestsFragment fragment = new RequestsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupFragmentView = inflater.inflate(R.layout.fragment_requests, container, false);
        initObjects();
        initRecyclerView();
        getTheRequestsData();
        return groupFragmentView;
    }

    void initObjects(){
        sharedPreference = new SharedPreference(getContext());
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        requestsRef = FirebaseDatabase.getInstance().getReference().child("Requests").child(firebaseUser.getUid());
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    void initRecyclerView() {
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(firebaseUser.getUid());
        recyclerView = groupFragmentView.findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onStart() {
        super.onStart();

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


    void initFirebaseAdapter(final String email , final String friendUid) {
        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(userRef, User.class)
                        .build();

        FirebaseRecyclerAdapter<User,FindFriendViewHolder> adapter = new FirebaseRecyclerAdapter<User, FindFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull final User model) {
                if (model.getEmail() == email)   {
                holder.userName.setText(model.getUsername());
                    holder.userStatus.setText(model.getAbout().trim());
                    if (!model.getImageUrl().equals(""))
                        Picasso.get().load(model.getImageUrl()).placeholder(R.drawable.default_profile_image).into(holder.profileImage);
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
                            intent.putExtra("isNewFriend",true);
                            intent.putExtra("friendUid" , friendUid);
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

    private void getTheRequestsData(){
        requestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    final String uid = ds.getKey();
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()){
                                final String userUid = child.getKey();
                                if (userUid.equals(uid)){
                                    Toast.makeText(getContext(), userUid, Toast.LENGTH_SHORT).show();
                                    userRef.child(userUid).child("email").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            // This method is called once with the initial value and again
                                            // whenever data at this location is updated.
                                            String email = dataSnapshot.getValue(String.class);
                                            initFirebaseAdapter(email , userUid);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError error) {
                                            // Failed to read value
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
