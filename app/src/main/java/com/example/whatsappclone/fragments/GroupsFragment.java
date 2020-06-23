package com.example.whatsappclone.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapters.ChatListAdapter;
import com.example.whatsappclone.adapters.GroupListAdapter;
import com.example.whatsappclone.models.GroupModelItem;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class GroupsFragment extends Fragment {
    View groupFragmentView;


    BottomSheetDialog bottomSheetDialog;
    View bottomSheetView;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public GroupsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupsFragment newInstance(String param1, String param2) {
        GroupsFragment fragment = new GroupsFragment();
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
/***************************************************************************************************/
    FirebaseDatabase database;
    DatabaseReference myRef;
    private ArrayList<GroupModelItem> groupModelItemArrayList;
    private RecyclerView recyclerView;
    private GroupListAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupFragmentView =  inflater.inflate(R.layout.fragment_groups, container, false);
        initObject();
        getGroupData();
        initView();
        if (groupModelItemArrayList != null)
            initRecyclerView();
        return groupFragmentView;
    }

    void initView(){

    }

    void initObject(){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        groupModelItemArrayList = new ArrayList<>();
    }

   void initRecyclerView(){
       recyclerView = groupFragmentView.findViewById(R.id.my_recycler_view);
       // use this setting to improve performance if you know that changes
       // in content do not change the layout size of the RecyclerView
       recyclerView.setHasFixedSize(true);

       // use a linear layout manager
       layoutManager = new LinearLayoutManager(getContext());
       recyclerView.setLayoutManager(layoutManager);

       // specify an adapter (see also next example)
       // put you list on the constructor
       mAdapter = new GroupListAdapter(groupModelItemArrayList , getContext());
       recyclerView.setAdapter(mAdapter);
    }

    void getGroupData(){
        myRef.child("Groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupModelItemArrayList.clear();
                for (DataSnapshot groupsSnapshot : dataSnapshot.getChildren()){
                    GroupModelItem groupModelItem = groupsSnapshot.getValue(GroupModelItem.class);
                    Log.d("data", "onDataChange: "+ groupModelItem.getGroupName());
                    groupModelItemArrayList.add(groupModelItem);
                }
                mAdapter.setList(groupModelItemArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /***********************************************************************************************/
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
