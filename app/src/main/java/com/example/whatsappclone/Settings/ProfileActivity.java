package com.example.whatsappclone.Settings;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.whatsappclone.R;
import com.example.whatsappclone.generalClasses.SharedPreference;
import com.example.whatsappclone.models.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;


public class ProfileActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_GET = 1;
    Toolbar toolbar;
    User userData;
    FirebaseDatabase database;
    DatabaseReference myRef;
    LinearLayout nameLinearLayout;
    LinearLayout aboutLinearLayout;
    LinearLayout phoneLinearLayout;
    BottomSheetDialog nameBottomSheetDialog;
    View nameBottomSheetView;
    BottomSheetDialog aboutBottomSheetDialog;
    View aboutBottomSheetView;
    BottomSheetDialog phoneBottomSheetDialog;
    View phoneBottomSheetView;
    EditText nameEditTxt;
    EditText aboutEditTxt;
    EditText phoneEditTxt;
    Button nameDoneBtn;
    Button aboutDoneBtn;
    Button phoneDoneBtn;
    FirebaseUser currentUser;
    TextView nameTxtView;
    TextView aboutTxtView;
    TextView phoneTxtView;
    ImageView profilePic;
    FloatingActionButton getImageBtn;
    SharedPreference sharedPreference;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private String currentUserID;
    private FirebaseAuth mAuth;
    private boolean isItMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initObjects();
        checkTheUser();
        initView();
        setView();
        if (isItMe) {
            clickEvents();
        }
    }

    void checkTheUser() {
        isItMe = getIntent().getBooleanExtra("isItMe", true);
        if (!isItMe)
            userData = getIntent().getParcelableExtra("User");
    }

    void init_toolbar() {
        toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("WhatsApp");
    }

    void initView() {
        toolbar = findViewById(R.id.settings_toolbar);
        nameTxtView = findViewById(R.id.name_txt_view);
        aboutTxtView = findViewById(R.id.about_txt_view);
        phoneTxtView = findViewById(R.id.phone_txt_view);
        nameLinearLayout = findViewById(R.id.edit_name);
        aboutLinearLayout = findViewById(R.id.about_linear_layout);
        phoneLinearLayout = findViewById(R.id.phone_linear_layout);
        profilePic = findViewById(R.id.profilePic);
        getImageBtn = findViewById(R.id.get_image_btn);
        init_toolbar();
        initSheetBottomView();
    }

    private void setView() {
        if (isItMe) {
            userData = sharedPreference.loadData();
        }
        nameTxtView.setText(userData.getUsername());
        aboutTxtView.setText(userData.getAbout());
        phoneTxtView.setText(userData.getPhone());
        nameEditTxt.setText(userData.getUsername());
        aboutEditTxt.setText(userData.getAbout());
        phoneEditTxt.setText(userData.getPhone());
        if (!userData.getImageUrl().trim().equals("")) {
            Log.d("saved image", userData.getImageUrl());
            Picasso.get().load(userData.getImageUrl()).fit().centerCrop()
                    .placeholder(R.drawable.default_profile_image)
                    .error(R.drawable.group_image)
                    .into(profilePic, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d("image put", "happened");
                            Log.d("what url", userData.getImageUrl());
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(ProfileActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        }
                    });

        } else {
            Log.d("image put", "does not happened");
            Log.d("what url", userData.getImageUrl());
        }

    }

    void initSheetBottomView() {
        // for name Bottom sheet
        nameBottomSheetDialog = new BottomSheetDialog(ProfileActivity.this, R.style.BottomSheetDialogTheme);
        nameBottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_name_bottom_sheet,
                (LinearLayout) findViewById(R.id.name_bottom_sheet_container));//here write the container id
        nameEditTxt = nameBottomSheetView.findViewById(R.id.name_edt_txt);
        nameDoneBtn = nameBottomSheetView.findViewById(R.id.name_done);

        // for about bottom sheet
        aboutBottomSheetDialog = new BottomSheetDialog(ProfileActivity.this, R.style.BottomSheetDialogTheme);
        aboutBottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_about_bottom_sheet,
                (LinearLayout) findViewById(R.id.about_bottom_sheet_container));//here write the container id
        aboutEditTxt = aboutBottomSheetView.findViewById(R.id.about_edt_txt);
        aboutDoneBtn = aboutBottomSheetView.findViewById(R.id.about_done);

        // for phone bottom sheet
        phoneBottomSheetDialog = new BottomSheetDialog(ProfileActivity.this, R.style.BottomSheetDialogTheme);
        phoneBottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_phone_bottom_sheet,
                (LinearLayout) findViewById(R.id.phone_bottom_sheet_container));//here write the container id
        phoneEditTxt = phoneBottomSheetView.findViewById(R.id.phone_edt_txt);
        phoneDoneBtn = phoneBottomSheetView.findViewById(R.id.phone_done);
    }

    void initObjects() {
        userData = new User();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        sharedPreference = new SharedPreference(this);
        mStorageRef = FirebaseStorage.getInstance().getReference().child("profile images");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = currentUser.getUid();
    }

    private void updateDatabase(User userData) {
        String userID = currentUser.getUid();
        sharedPreference.saveData(userData);
        Toast.makeText(this, sharedPreference.loadData().getUsername(), Toast.LENGTH_SHORT).show();
        myRef.child("Users").child(userID).setValue(userData);
        myRef.child("Users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User value = dataSnapshot.getValue(User.class);
                sharedPreference.saveData(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    void clickEvents() {
        nameLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameBottomSheetDialog.setContentView(nameBottomSheetView);
                nameBottomSheetDialog.show();
            }
        });
        /*******************************************************************************************/
        aboutLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutBottomSheetDialog.setContentView(aboutBottomSheetView);
                aboutBottomSheetDialog.show();
            }
        });
        /*******************************************************************************************/
        phoneLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneBottomSheetDialog.setContentView(phoneBottomSheetView);
                phoneBottomSheetDialog.show();
            }
        });
        /*******************************************************************************************/
        nameDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!nameEditTxt.getText().toString().equals("")) {
                    setUserData();
                }
                nameBottomSheetDialog.dismiss();
            }
        });
        /*******************************************************************************************/
        aboutDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!aboutEditTxt.getText().toString().equals("")) {
                    setUserData();
                }
                aboutBottomSheetDialog.dismiss();
            }
        });
        /*******************************************************************************************/
        phoneDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!phoneEditTxt.getText().toString().equals("")) {
                    setUserData();
                }
                phoneBottomSheetDialog.dismiss();
            }
        });
        /*******************************************************************************************/
        getImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            Uri fullPhotoUri = data.getData();
            CropImage.activity(fullPhotoUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri cropedImage = result.getUri();
                uploadFile(cropedImage);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void setUserData() {
        userData.setUsername(nameEditTxt.getText().toString());
        userData.setPhone(phoneEditTxt.getText().toString());
        userData.setAbout(aboutEditTxt.getText().toString());
        getImageUrlFromFirebaseDatabase();
        updateDatabase(userData);
        setView();
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
    }

    private void uploadFile(final Uri mImageUri) {
        if (mImageUri != null) {
            final StorageReference imagesFileRefrence = mStorageRef.child(currentUserID
                    + "." + getFileExtension(mImageUri));

            imagesFileRefrence.putFile(mImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return imagesFileRefrence.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Profile Image uploaded Successfully...", Toast.LENGTH_SHORT).show();
                        final String downloadUrl = task.getResult().toString();
                        Log.d("download url", downloadUrl);
                        setImageUrlIntoFirebaseDatabase(downloadUrl);
                    }
                }
            });
        }
    }

    void setImageUrlIntoFirebaseDatabase(final String downloadUrl) {
        mDatabaseRef.child("Users").child(currentUserID).child("imageUrl")
                .setValue(downloadUrl)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("setting image ", "happened");
                            Log.d("saved image", downloadUrl);
                            Toast.makeText(ProfileActivity.this, "Image saved in Database, Successfully...", Toast.LENGTH_SHORT).show();
                            userData.setImageUrl(downloadUrl);
                            setUserData();
                            //loadingBar.dismiss();
                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(ProfileActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            //loadingBar.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("setting image ", "does not happened");

            }
        });
    }

    void getImageUrlFromFirebaseDatabase() {
        mDatabaseRef.child("Users").child(currentUserID).child("imageUrl")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        String imageUrl = dataSnapshot.getValue(String.class);
                        userData.setImageUrl(imageUrl);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w("Reading form database", "Failed to read value.", error.toException());
                    }
                });
    }
}