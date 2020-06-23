package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import com.example.whatsappclone.Settings.ProfileActivity;
import com.example.whatsappclone.Settings.SettingsActivity;
import com.example.whatsappclone.fragments.TabAccessorAdapter;
import com.example.whatsappclone.generalClasses.GeneralPurposes;
import com.example.whatsappclone.generalClasses.SharedPreference;
import com.example.whatsappclone.models.User;
import com.example.whatsappclone.models.GroupModelItem;
import com.example.whatsappclone.registiration.LoginActivity;
import com.example.whatsappclone.registiration.RegisterActivity;
import com.example.whatsappclone.services.MessagesNotificationService;
import com.example.whatsappclone.viewmodels.ChatsViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabAccessorAdapter tabAccessorAdapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private User userData;
    private GeneralPurposes generalPurposes;
    SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initObjects();
        generalPurposes.setMeOnline();
        checkUser(firebaseUser);
        createDatabase();
        getData(firebaseUser);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        generalPurposes.setMeOffline();
        fireService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        generalPurposes.deleteNotification();
    }

    void initView() {
        init_toolbar();
        init_viewPager();
        init_tab_layout();
    }

    void initObjects() {
        generalPurposes = new GeneralPurposes(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        sharedPreference = new SharedPreference(this);
        database = FirebaseDatabase.getInstance();
        userData = new User();
        myRef = database.getReference();
    }

    void init_toolbar() {
        toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("WhatsApp");
    }

    void init_viewPager() {
        viewPager = findViewById(R.id.main_tab_pager);
        tabAccessorAdapter = new TabAccessorAdapter(getSupportFragmentManager(), 3);
        viewPager.setAdapter(tabAccessorAdapter);
    }

    void init_tab_layout() {
        tabLayout = findViewById(R.id.main_tab);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void checkUser(FirebaseUser currentUser) {
        if (currentUser == null) {
            //go to login acivity
            Toast.makeText(this, "firebase User is null", Toast.LENGTH_SHORT).show();
            goToRegisterActivity();
            return;
        }
        if (!currentUser.isEmailVerified()) {
            goToRegisterActivity();
            Toast.makeText(this, "Verify you account.", Toast.LENGTH_SHORT).show();
        }
        verifyUserExistance();
    }

    public void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public void goToRegisterActivity(){
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    void goToSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    void verifyUserExistance() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String UID = firebaseUser.getUid();
        // Read from the database
        myRef.child("Users").child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user2 = dataSnapshot.getValue(User.class);
                if (user2.getUsername() == "unknown")
                    goToProfileActivity();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void goToProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("the profile for","me");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void goToFindFriendsActivity(){
        Intent intent = new Intent(this,FindFriendsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.new_group:
                createDialogBox();
                return true;
            case R.id.settings:
                goToSettingsActivity();
                return true;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                goToLoginActivity();
                return true;
            case R.id.Find_Friends:
                goToFindFriendsActivity();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void getData(FirebaseUser firebaseUser){
        if (firebaseUser != null) {
            myRef.child("Users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        userData = child.getValue(User.class);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("getting data", "Failed to read value.", error.toException());
                }
            });
            userData = sharedPreference.loadData();
            userData.setEmail(firebaseUser.getEmail());
            sharedPreference.saveData(userData);
            myRef.child("Users").child(firebaseUser.getUid()).setValue(sharedPreference.loadData());
        }
    }

    private void createDatabase() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String userID = firebaseUser.getUid();
            myRef.child("Users").child(userID);
        }
    }

    void createDialogBox(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this , R.style.AlertDialog);
        final EditText groupNameEdtTxt = new EditText(getApplicationContext());
        alert.setMessage("Write group name")//Here the message you want to show
                .setTitle("Group name")//Here the Title
                .setView(groupNameEdtTxt)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String groupName;
                        groupName = groupNameEdtTxt.getText().toString();
                        if (groupName != ""){
                            createGroup(groupName);
                        }else {
                            Toast.makeText(getApplicationContext(), "Write group name", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).show();
    }

    public void createGroup(String groupName) {
        GroupModelItem groupModelItem = new GroupModelItem();
        groupModelItem.setGroupName(groupName);
        groupModelItem.setLastMessage("");
        groupModelItem.setImage(R.drawable.group_image);
        groupModelItem.setParticipatedPeople("");
        myRef.child("Groups").child(groupName).setValue(groupModelItem).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(MainActivity.this, "Created successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void fireService(){
        Intent serviceIntent = new Intent(this, MessagesNotificationService.class);
        serviceIntent.putExtra("myUid", firebaseUser.getUid());
        MessagesNotificationService.enqueueWork(this , serviceIntent);

    }
}
