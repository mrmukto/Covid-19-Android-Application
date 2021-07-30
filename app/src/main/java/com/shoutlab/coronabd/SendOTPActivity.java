package com.shoutlab.coronabd;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

import java.util.Objects;

public class SendOTPActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private CountryCodePicker code_picker;
    private Button next_button;
    private ImageView mobile_valid_sign;
    private AlertDialog alertDialog;
    private String mobile_number_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_o_t_p);

        preferenceManager = new PreferenceManager(SendOTPActivity.this);

        code_picker = findViewById(R.id.countryCodeHolder);
        EditText mobile_number = findViewById(R.id.mobile_number);
        next_button = findViewById(R.id.next_button);
        mobile_valid_sign = findViewById(R.id.mobile_valid_sign);
        TextView mobile_title = findViewById(R.id.mobile_title);
        TextView agreement = findViewById(R.id.agreement);
        ImageView back_button = findViewById(R.id.back_button);

        if(preferenceManager.getLanguage().equals("BN")){
            next_button.setText("পরবর্তী");
            mobile_title.setText("মোবাইল নাম্বারটি দিন");
            agreement.setText(getResources().getString(R.string.agreement_bn));
        }

        code_picker.registerCarrierNumberEditText(mobile_number);

        code_picker.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                if(isValidNumber){
                    mobile_valid_sign.setVisibility(View.VISIBLE);
                    next_button.setBackground(getResources().getDrawable(R.drawable.register_button));
                    next_button.setEnabled(true);
                } else {
                    mobile_valid_sign.setVisibility(View.INVISIBLE);
                    next_button.setBackground(getResources().getDrawable(R.drawable.register_disabled_button));
                    next_button.setEnabled(false);
                }
            }
        });

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mobile_number_str = code_picker.getFullNumberWithPlus();
                confirmNumber(mobile_number_str);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @SuppressLint("InflateParams")
    private void confirmNumber(String number){
        // Get screen width and height in pixels
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // The absolute width of the available display size in pixels.
        int displayWidth = displayMetrics.widthPixels;
        // Initialize a new window manager layout parameters
        // Set alert dialog width equal to screen width 70%
        int dialogWindowWidth = (int) (displayWidth * 0.8f);
        // Set alert dialog height equal to screen height 70%
        int dialogWindowHeight = (int) (displayWidth * 0.7f);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SendOTPActivity.this);
        View view;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view = LayoutInflater.from(SendOTPActivity.this).inflate(R.layout.popup_confirm_number, null);
        } else {
            view = LayoutInflater.from(SendOTPActivity.this).inflate(R.layout.popup_confirm_number_old, null);
        }
        TextView confirm_title = view.findViewById(R.id.confirm_title);
        TextView confirm_message = view.findViewById(R.id.confirm_message);
        confirm_message.setText(String.format("%s %s", getResources().getString(R.string.confirm_message), number));
        TextView cancel_otp = view.findViewById(R.id.cancel_otp);
        cancel_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        TextView send_otp = view.findViewById(R.id.send_otp);
        send_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SendOTPActivity.this, VerifyOTPActivity.class);
                intent.putExtra("MOBILE_NUMBER", mobile_number_str);
                startActivity(intent);
                alertDialog.dismiss();
            }
        });

        if(preferenceManager.getLanguage().equals("BN")){
            confirm_title.setText("কনফার্ম");
            confirm_message.setText(String.format("%s %s", getResources().getString(R.string.confirm_message_bn), number));
            cancel_otp.setText("ক্যান্সেল");
            send_otp.setText("ওটিপি পাঠান");
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
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SendOTPActivity.this);
        View view;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view = LayoutInflater.from(SendOTPActivity.this).inflate(R.layout.popup_loading, null);
        } else {
            view = LayoutInflater.from(SendOTPActivity.this).inflate(R.layout.popup_loading_old, null);
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
}
