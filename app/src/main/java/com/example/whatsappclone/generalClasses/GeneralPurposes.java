package com.example.whatsappclone.generalClasses;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.whatsappclone.generalClasses.SharedPreference;
import com.example.whatsappclone.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class GeneralPurposes {
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    SharedPreference sharedPreference;
    Context context;
    User user;
    private static final String TAG = "Completion";
    public GeneralPurposes(Context context){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = firebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        this.context = context;
        sharedPreference = new SharedPreference(context);
        user = new User();
        user = sharedPreference.loadData();
    }

    public void setMeOnline(){
        user.setOnline(true);
        sharedPreference.saveData(user);
        if (firebaseUser != null) {
            myRef.child("Users").child(firebaseUser.getUid()).child("online")
                    .setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(TAG, "completedSuccessfully ");
                }
            });
        }
    }

    public void setMeOffline(){
        user.setOnline(false);
        sharedPreference.saveData(user);
        myRef.child("Users").child(firebaseUser.getUid()).child("online")
                .setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "completedSuccessfully ");
            }
        });
    }

    public void deleteNotification(){
        Query query = myRef.child("Notifications").child(firebaseUser.getUid())
                .orderByChild("messageContent");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    dataSnapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Database delete" , "onCancelled", databaseError.toException());
            }
        });

    }
}
