package com.example.whatsappclone.registiration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.example.whatsappclone.MainActivity;
import com.example.whatsappclone.R;
import com.example.whatsappclone.generalClasses.SharedPreference;
import com.example.whatsappclone.models.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    final static String TAG = "Google authentication";
    private static final int RC_SIGN_IN = 1;
    EditText emailEdtTxt;
    EditText passwordEdtTxt;
    MaterialRippleLayout signBtn;
    MaterialRippleLayout facebookBtn;
    MaterialRippleLayout googleBtn;
    MaterialRippleLayout phoneBtn;
    TextView forgetPasswordTxt;
    TextView registerTxt;
    CallbackManager callbackManager;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ProgressDialog loadingBar;
    private GoogleSignInClient mGoogleSignInClient;
    SharedPreference sharedPreference;
    User userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initObjects();
        initView();
        clickEvents();
        init_googleSignIn();
    }

    private void initObjects() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        loadingBar = new ProgressDialog(this);
        callbackManager = CallbackManager.Factory.create();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        sharedPreference = new SharedPreference(this);
        userData = new User();
    }

    void initView() {
        emailEdtTxt = findViewById(R.id.email_edt_txt);
        passwordEdtTxt = findViewById(R.id.password_edt_txt);
        signBtn = findViewById(R.id.rippleSignIn);
        facebookBtn = findViewById(R.id.rippleFacebook);
        googleBtn = findViewById(R.id.rippleGoogle);
        phoneBtn = findViewById(R.id.ripplePhone);
        forgetPasswordTxt = findViewById(R.id.forget_password_txt);
        registerTxt = findViewById(R.id.register_edt_txt);
    }

    void clickEvents() {
        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithEmailAndPassword();
            }
        });
        /********************************************************************/
        facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginFacebookClick();
            }
        });
        /********************************************************************/
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUsingGoogle();
            }
        });
        /*********************************************************************/
        phoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPhoneActivity();
            }
        });
        /**********************************************************************/
        forgetPasswordTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        /***********************************************************************/
        registerTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRigisterActivity();
            }
        });

    }

    public void goToRigisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void goToPhoneActivity() {
        Intent intent = new Intent(this, PhoneLoginActivity.class);
        startActivity(intent);
    }

    public void goToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void signInWithEmailAndPassword() {
        String email = emailEdtTxt.getText().toString();
        String password = passwordEdtTxt.getText().toString();
        if (validateEmail(email) && validatePassword(password)) {
            startDialogBar();
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in firebaseUser's information
                                Log.d("Sign in :", "signInWithEmail:success");
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                if (user.isEmailVerified()) {
                                    loadingBar.dismiss();
                                    goToMainActivity();
                                } else {
                                    loadingBar.dismiss();
                                    Toast.makeText(getApplicationContext(), "Verify your accout", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // If sign in fails, display a message to the firebaseUser.
                                loadingBar.dismiss();
                                Log.w("Sign in : ", "signInWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            emailEdtTxt.setError("Field can't be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEdtTxt.setError("Please enter a valid email address");
            return false;
        } else {
            emailEdtTxt.setError(null);
            return true;
        }

    }

    private boolean validatePassword(String password) {
        Pattern PASSWORD_PATTERN =
                Pattern.compile("^" +
                        //"(?=.*[0-9])" +         //at least 1 digit
                        //"(?=.*[a-z])" +         //at least 1 lower case letter
                        //"(?=.*[A-Z])" +         //at least 1 upper case letter
                        "(?=.*[a-zA-Z])" +      //any letter
                        //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                        "(?=\\S+$)" +           //no white spaces
                        ".{4,}" +               //at least 4 characters
                        "$");
        if (password.isEmpty()) {
            passwordEdtTxt.setError("Field can't be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            passwordEdtTxt.setError("Password too weak");
            return false;
        } else {
            passwordEdtTxt.setError(null);
            return true;
        }
    }

    private void startDialogBar() {
        loadingBar.setTitle("Creating new account");
        loadingBar.setMessage("Please wait while creating the account ....");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();
    }

    public void loginFacebookClick() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback((callbackManager), new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                facebookSignInFirebase(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Authentication failed. try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void facebookSignInFirebase(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in firebaseUser's information
                            Log.d("AccessToken", "signInWithCredential:success");
                            goToMainActivity();
                        } else {
                            // If sign in fails, display a message to the firebaseUser.
                            Log.w("AccessToken", "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    void init_googleSignIn() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void signInUsingGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in firebaseUser's information
                            Log.d(TAG, "signInWithCredential:success");
                            goToMainActivity();
                        } else {
                            // If sign in fails, display a message to the firebaseUser.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "" + "Authentication failed", Toast.LENGTH_SHORT).show();
                            goToMainActivity();
                        }
                    }
                });
    }

    /*private void getUserData(){
        myRef.child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                userData = dataSnapshot.getValue(User.class);
                Toast.makeText(LoginActivity.this, userData.getUsername(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Reading Data", "Failed to read value.", error.toException());
            }
        });

        sharedPreference.saveData(userData);
    }
    */
}
