package com.shoutlab.coronabd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukesh.OtpView;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerifyOTPActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private AlertDialog alertDialog;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private boolean WaitForOTP = false;
    private OtpView otpView;
    private String otp_code = "", mobile_number_str;
    private TextView invalid_otp, resend_countdown, resend_button, otp_title, otp_details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_o_t_p);

        preferenceManager = new PreferenceManager(VerifyOTPActivity.this);
        mAuth = FirebaseAuth.getInstance();

        otpView = findViewById(R.id.otp_code);
        Button verify_button = findViewById(R.id.verify_button);
        invalid_otp = findViewById(R.id.invalid_otp);
        ImageView back_button = findViewById(R.id.back_button);
        TextView edit_button = findViewById(R.id.edit_button);
        TextView mobile_number = findViewById(R.id.mobile_number);
        resend_countdown = findViewById(R.id.resend_countdown);
        resend_button = findViewById(R.id.resend_button);
        otp_title = findViewById(R.id.otp_title);
        otp_details = findViewById(R.id.otp_details);

        if(preferenceManager.getLanguage().equals("BN")){
            otp_title.setText("ওটিপি ভেরিফিকেশন");
            otp_details.setText("আপনাকে প্রেরিত ৬-সংখ্যার কোড লিখুন");
            edit_button.setText("ইডিট");
            verify_button.setText("ভেরিফাই");
            resend_button.setText("কোড পুনরায় পাঠান");
        }

        Intent intent = getIntent();
        mobile_number_str = intent.getStringExtra("MOBILE_NUMBER");
        sendOTP(mobile_number_str);
        mobile_number.setText(mobile_number_str);
        OTPCountdown();

        otpView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                otp_code = editable.toString();
            }
        });

        verify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(otp_code.length() == 6){
                    invalid_otp.setVisibility(View.GONE);
                    verifyVerificationCode(otp_code);
                } else {
                    invalid_otp.setVisibility(View.VISIBLE);
                }
            }
        });

        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        resend_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendOTP(mobile_number_str, mResendToken);
                OTPCountdown();
            }
        });
    }

    private void resendOTP(String mobile, PhoneAuthProvider.ForceResendingToken mResendToken) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks,
                mResendToken);
    }

    private void OTPCountdown(){
        new CountDownTimer(60000, 1000){
            @SuppressLint("DefaultLocale")
            @Override
            public void onTick(long millisUntilFinished) {
                resend_countdown.setVisibility(View.VISIBLE);
                resend_button.setVisibility(View.GONE);
                if(preferenceManager.getLanguage().equals("BN")){
                    resend_countdown.setText(String.format("কোড পুনরায় পাঠান %ds", millisUntilFinished / 1000));
                } else {
                    resend_countdown.setText(String.format("Resend code in %ds", millisUntilFinished / 1000));
                }
            }

            @Override
            public void onFinish() {
                resend_countdown.setVisibility(View.GONE);
                resend_button.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void sendOTP(String mobile_number_str) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile_number_str,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId = s;
            mResendToken = forceResendingToken;
            WaitForOTP = true;
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(VerifyOTPActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();
            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                otpView.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            } else {
                if(!WaitForOTP){
                    showLoading();
                    signInWithPhoneAuthCredential(phoneAuthCredential);
                }
            }
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerifyOTPActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dismissLoading();
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            preferenceManager.setIsLoggedIn();
                            preferenceManager.setUUID(mobile_number_str);
                            Intent intent = new Intent(VerifyOTPActivity.this, CreateProfileActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        } else {
                            //verification unsuccessful
                            String message = "Something went wrong. Please try again.";
                            String message_bn = "কিছু ভুল হয়েছে। আবার চেষ্টা করুন।";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered.";
                                message_bn = "আপনি ভুল কোড দিয়েছেন।";
                            }

                            if(preferenceManager.getLanguage().equals("BN")){
                                Toast.makeText(VerifyOTPActivity.this, message_bn, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(VerifyOTPActivity.this, message, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(VerifyOTPActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }});
    }

    private void verifyVerificationCode(String code) {
        showLoading();

        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    @SuppressLint("InflateParams")
    private void showLoading(){
        // Get screen width and height in pixels
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // The absolute width of the available display size in pixels.
        int displayWidth = displayMetrics.widthPixels;
        // Initialize a new window manager layout parameters
        // Set alert dialog width equal to screen width 70%
        int dialogWindowWidth = (int) (displayWidth * 0.4f);
        // Set alert dialog height equal to screen height 70%
        int dialogWindowHeight = (int) (displayWidth * 0.4f);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VerifyOTPActivity.this);
        View view;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view = LayoutInflater.from(VerifyOTPActivity.this).inflate(R.layout.popup_loading, null);
        } else {
            view = LayoutInflater.from(VerifyOTPActivity.this).inflate(R.layout.popup_loading_old, null);
        }
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setView(view);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
            layoutParams.width = dialogWindowWidth;
            layoutParams.height = dialogWindowHeight;
            alertDialog.getWindow().setAttributes(layoutParams);
        }
    }

    private void dismissLoading(){
        alertDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
