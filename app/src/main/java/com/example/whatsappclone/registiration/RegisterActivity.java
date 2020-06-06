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

public class RegisterActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    final static String TAG = "Google authentication";
    EditText emailEdtTxt;
    EditText passwordEdtTxt;
    EditText confirmPasswordEdtTxt;
    MaterialRippleLayout registerBtn;
    MaterialRippleLayout facebookBtn;
    MaterialRippleLayout googleBtn;
    MaterialRippleLayout phoneBtn;
    TextView signInTxt;
    private FirebaseAuth firebaseAuth;
    CallbackManager callbackManager;
    private ProgressDialog loadingBar;
    private GoogleSignInClient mGoogleSignInClient;
    FirebaseDatabase database;
    FirebaseUser user;
    DatabaseReference myRef;
    SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        initObjects();
        clickEvents();
        init_googleSignIn();
    }

    void initObjects(){
        firebaseAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        sharedPreference = new SharedPreference(this);
    }

    void initView() {
        emailEdtTxt = findViewById(R.id.email_edt_txt);
        passwordEdtTxt = findViewById(R.id.password_edt_txt);
        confirmPasswordEdtTxt = findViewById(R.id.confirm_password_edt_txt);
        registerBtn = findViewById(R.id.register_btn);
        facebookBtn = findViewById(R.id.rippleFacebook);
        googleBtn = findViewById(R.id.ripple_google);
        phoneBtn = findViewById(R.id.ripple_phone);
        signInTxt = findViewById(R.id.sign_in_txt);
    }

    public void goToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
        startActivity(intent);
    }

    void clickEvents() {
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEmail();
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
        /***********************************************************************/
        signInTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginActivity();
            }
        });

    }

    public void goToPhoneActivity() {
        Intent intent = new Intent(this, PhoneLoginActivity.class);
        startActivity(intent);
    }

    public void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    void createEmail() {
        final String email = emailEdtTxt.getText().toString();
        final String password = passwordEdtTxt.getText().toString();
        final String confirmPassword = confirmPasswordEdtTxt.getText().toString();
        if (validateEmail(email) && validatePassword(password, confirmPassword)) {
            startDialogBar();
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Note : ", "createUserWithEmail:success");
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("Email : ", "sent.");
                                                    Toast.makeText(getApplicationContext(),
                                                            "Email created successfully,please check your email for verification",
                                                            Toast.LENGTH_LONG).show();
                                                    emailEdtTxt.setText(null);
                                                    passwordEdtTxt.setText(null);
                                                    confirmPasswordEdtTxt.setText(null);
                                                    loadingBar.dismiss();
                                                    createDatabase();
                                                    goToLoginActivity();
                                                }
                                            }
                                        });
                            } else {
                                // If sign in fails, display a message to the user.
                                loadingBar.dismiss();
                                Log.w("Note : ", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
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

    private boolean validatePassword(String password, String confirmPassword) {
        Pattern PASSWORD_PATTERN =
                Pattern.compile("^" +
                        //"(?=.*[0-9])" +         //at least 1 digit
                        //"(?=.*[a-z])" +         //at least 1 lower case letter
                        //"(?=.*[A-Z])" +         //at least 1 upper case letter
                        "(?=.*[a-zA-Z])" +      //any letter
                        //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                        "(?=\\S+$)" +           //no white spaces
                        ".{6,}" +               //at least 6 characters
                        "$");
        if (password.isEmpty()) {
            passwordEdtTxt.setError("Field can't be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            passwordEdtTxt.setError("Password too weak");
            return false;
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "" + password + "," + confirmPassword, Toast.LENGTH_LONG).show();
            passwordEdtTxt.setError("password doesn't match!!");
            confirmPasswordEdtTxt.setError("password doesn't match!!");
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
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("AccessToken", "signInWithCredential:success");
                            createDatabase();
                            goToMainActivity();
                        } else {
                            // If sign in fails, display a message to the user.
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

    void init_googleSignIn(){
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
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            createDatabase();
                            goToMainActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), ""+"Authentication failed", Toast.LENGTH_SHORT).show();
                            goToMainActivity();
                        }

                        // ...
                    }
                });
    }

    private void createDatabase(){
        user = firebaseAuth.getCurrentUser();
        String userID = user.getUid();
        myRef.child("Users").child(userID).setValue("");
    }

}
