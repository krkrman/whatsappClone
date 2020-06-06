package com.example.whatsappclone.registiration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.example.whatsappclone.MainActivity;
import com.example.whatsappclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {
    Toolbar toolbar;
    Button sendVerificationCodeBtn , verifyBtn;
    MaterialRippleLayout sendVerificationCodeRipple , verifyRipple;
    EditText phoneEditTxt , verificationEditTxt;
    TextView verificationTxtView , timerTxtView;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;

    private ProgressDialog loadingBar;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    MyThread myThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        initView();
        clickEvents();
        initObjects();
    }
    void initToolbar(){
        toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        getSupportActionBar().setTitle("WhatsApp");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    void initView(){
        sendVerificationCodeBtn = findViewById(R.id.send_verification_code_btn);
        verifyBtn = findViewById(R.id.verification_btn);
        sendVerificationCodeRipple = findViewById(R.id.ripple_send_verification_code);
        verifyRipple = findViewById(R.id.ripple_send_verification_code);
        phoneEditTxt = findViewById(R.id.phone_edt_txt);
        verificationEditTxt = findViewById(R.id.verification_edt_txt);
        verificationTxtView = findViewById(R.id.verification_txt_view);
        loadingBar = new ProgressDialog(this);
        timerTxtView = findViewById(R.id.timer_txt_view);
        initToolbar();
    }

    void initObjects(){
        initCallbacks();
        mAuth = FirebaseAuth.getInstance();
    }

    void clickEvents(){
        sendVerificationCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isThereIsInternetConnection()){
                    showProgressBar();
                    String phoneNumber = phoneEditTxt.getText().toString();
                    if (phoneNumber.trim() != ""){
                        // This code is to send verification code
                        Toast.makeText(PhoneLoginActivity.this, phoneNumber, Toast.LENGTH_SHORT).show();
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                phoneNumber,        // Phone number to verify
                                60,                 // Timeout duration
                                TimeUnit.SECONDS,   // Unit of timeout
                                PhoneLoginActivity.this,               // Activity (for callback binding)
                                mCallbacks);        // OnVerificationStateChangedCallbacks
                    }else {
                        Toast.makeText(PhoneLoginActivity.this, "Phone number is required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String verificationCode = verificationEditTxt.getText().toString();
                if (verificationCode != ""){
                    showProgressBar();
                    // authentication code
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }else {
                    Toast.makeText(PhoneLoginActivity.this, "Please write verification code first...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void initCallbacks(){
        // init callbacks
        mCallbacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d("Verification", "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("Verification", "onVerificationFailed", e);
                Toast.makeText(PhoneLoginActivity.this, "Invalid Phone Number, Please enter correct phone number with your country code...", Toast.LENGTH_LONG).show();
                loadingBar.dismiss();
                updateUIWhenVerificationFailed();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Log.w("Verification", "Invalid request");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Log.w("Verification", "SMS quota for the project has been exceeded ");
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("code is sent", "onCodeSent:" + verificationId);
                Toast.makeText(PhoneLoginActivity.this, "Code has been sent, please check and verify...", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
                // Save verification ID and resending token so we can use them later
                updateUIWhenCodeSent();
                mVerificationId = verificationId;
                mResendToken = token;

                // ...
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            loadingBar.dismiss();
                            Toast.makeText(PhoneLoginActivity.this, "Congratulations, you're logged in Successfully.", Toast.LENGTH_SHORT).show();
                            goToMainActivity();
                            updateUIWhenCodeSent();
                            Log.d("Authentication : ", "signInWithCredential:success");

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("Authentication : ", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid

                            }
                        }
                    }
                });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    boolean isThereIsInternetConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        }
        else
            return false;
    }

    void updateUIWhenCodeSent(){
        verifyBtn.setVisibility(View.VISIBLE);
        verificationTxtView.setVisibility(View.VISIBLE);
        verificationEditTxt.setVisibility(View.VISIBLE);
        timerTxtView.setVisibility(View.VISIBLE);
        phoneEditTxt.setEnabled(false);
        sendVerificationCodeBtn.setEnabled(false);
        startTimer();
    }

    void updateUIWhenVerificationFailed(){
        timerTxtView.setVisibility(View.GONE);
        timerTxtView.setText("60");
        phoneEditTxt.setEnabled(true);
        sendVerificationCodeBtn.setEnabled(true);
    }

    class MyThread extends Thread   {
        int timer = 60;
        @Override
        public void run() {
            while (true){
                //Anonymous inner classes
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //you can acccess to any UI element here
                        timer--;
                        if (timer >= 0)
                            timerTxtView.setText(String.valueOf(timer));
                        else {
                            updateUIWhenVerificationFailed();
                            return;
                        }
                    }
                });
                try {
                    Thread.sleep(1000);//this is the time in ms
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void startTimer(){
        myThread =new MyThread();
        myThread.start();
    }

    void showProgressBar(){
        loadingBar.setTitle("Phone Verification");
        loadingBar.setMessage("Please wait ");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
    }
}
