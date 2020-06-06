package com.example.whatsappclone.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsappclone.R;
import com.example.whatsappclone.generalClasses.SharedPreference;
import com.example.whatsappclone.models.User;
import com.example.whatsappclone.registiration.LoginActivity;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {
    LinearLayout profileLayout;
    LinearLayout wallpaper;
    LinearLayout notifiction;
    LinearLayout deleteAccount;
    SharedPreference sharedPreference;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    TextView nameTxtView;
    TextView aboutTxtView;
    User user;
    Toolbar toolbar;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initObjects();
        initView();
        clickEvents();
        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, sharedPreference.loadData().getUsername(), Toast.LENGTH_SHORT).show();
        updateUI();
    }

    void initView(){
        profileLayout = findViewById(R.id.profile_layout);
        wallpaper = findViewById(R.id.wallpaper_linear_layout);
        notifiction = findViewById(R.id.notification_linear_layout);
        deleteAccount = findViewById(R.id.delete_linear_layout);
        nameTxtView = findViewById(R.id.name_txt_view);
        aboutTxtView = findViewById(R.id.about_txt_view);
        initToolbar();
    }

    void initObjects(){
        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreference = new SharedPreference(this);
        user = new User();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
    }

    void initToolbar(){
        toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//to put arrow back
        getSupportActionBar().setTitle("SettingsActivity");
    }

    void goToProfileActivity(){
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("isItMe",true);
        startActivity(intent);
    }

    public void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    void clickEvents(){
        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfileActivity();
            }
        });
        /******************************************************************************************/
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog();
            }
        });
    }

    void updateUI(){
        String currentId = firebaseAuth.getCurrentUser().getUid();
        //myRef.child("Users").child(currentId).addListenerForSingleValueEvent();
        user = sharedPreference.loadData();
        nameTxtView.setText(user.getUsername());
        aboutTxtView.setText(user.getAbout());
    }

    void createDialog(){
        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        alert.setMessage("Are you sure you want to delete")//Here the message you want to show
                .setTitle("Confirmation")//Here the Title
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAccount();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //write what you want to do when click no
                    }
                }).show();


    }

    void deleteAccount() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String password = "2270805kemo";
        AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), password);

        // Prompt the user to re-provide their sign-in credentials
        firebaseUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        firebaseUser.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("Delete : ", "User account deleted.");
                                            Toast.makeText(SettingsActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                            deleteAccountDatabase();
                                            goToLoginActivity();
                                        }
                                    }
                                });
                    }
                }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Toast.makeText(SettingsActivity.this, "somethin error ", Toast.LENGTH_SHORT).show();

            }
        });
    }

    void deleteAccountDatabase(){
        Query applesQuery = myRef.child("Users").orderByChild("username").equalTo(sharedPreference.loadData().getUsername());
        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Database delete" , "onCancelled", databaseError.toException());
            }
        });
    }

}
