package com.shoutlab.coronabd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class CovidReport extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_report);

        PreferenceManager preferenceManager = new PreferenceManager(CovidReport.this);

        View good_report = findViewById(R.id.good_report);
        View bad_report = findViewById(R.id.bad_report);
        TextView safetyText = findViewById(R.id.safetyText);
        TextView riskText = findViewById(R.id.riskText);
        ImageView back_button = findViewById(R.id.back_button);
        CardView recheckButton = findViewById(R.id.recheckButton);
        TextView SSReport = findViewById(R.id.SSReport);
        CardView coronaHospitals = findViewById(R.id.coronaHospitals);
        CardView helpLine = findViewById(R.id.helpLine);
        BlurView blurView = findViewById(R.id.blurView);

        float radius = 20f;

        ViewGroup rootView = findViewById(R.id.root);
        Drawable windowBackground = getWindow().getDecorView().getBackground();

        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(true);

        Intent intent = getIntent();
        String result = intent.getStringExtra("RESULT");
        String resultBN = intent.getStringExtra("RESULT_BN");
        if(result != null){
            int resultInt = Integer.parseInt(result);
            if(resultInt == 0){
                SSReport.setVisibility(View.VISIBLE);
                good_report.setVisibility(View.VISIBLE);
                safetyText.setText(String.format("আপনার স্কোর %s\nআপনি সম্পূর্ণ সুস্থ আছেন", resultBN));
                bad_report.setVisibility(View.GONE);
            } else if(resultInt <= 2){
                SSReport.setVisibility(View.VISIBLE);
                good_report.setVisibility(View.VISIBLE);
                safetyText.setText(String.format("আপনার স্কোর %s\nআপনার লক্ষণগুলো মানসিক চাপ ও ভয় থেকে হতে পারে", resultBN));
                bad_report.setVisibility(View.GONE);
            } else if(resultInt <= 5) {
                SSReport.setVisibility(View.VISIBLE);
                good_report.setVisibility(View.VISIBLE);
                safetyText.setText(String.format("আপনার স্কোর %s\nবেশি বেশি পানি পান করতে হবে ও পরিচ্ছন্নতা মেনে চলতে হবে। ২ দিন পর পর অবস্থার উন্নতি হল কি না তা খেয়াল রাখতে হবে", resultBN));
                bad_report.setVisibility(View.GONE);
            } else if(resultInt <= 12) {
                good_report.setVisibility(View.GONE);
                bad_report.setVisibility(View.VISIBLE);
                riskText.setText(String.format("আপনার স্কোর %s\nআপনার দ্রুত ডাক্তারের সাথে কথা বলতে হবে এবং প্রয়োজনীয় ব্যাবস্থা নিতে হবে", resultBN));

            } else if(resultInt <= 24) {
                good_report.setVisibility(View.GONE);
                bad_report.setVisibility(View.VISIBLE);
                riskText.setText(String.format("আপনার স্কোর %s\nআপনার IEDCR এ যোগাযোগ করতে হবে।", resultBN));

            }
        } else {
            Intent intent2 = new Intent(CovidReport.this, TestInfectionActivity.class);
            startActivity(intent2);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        }

        recheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CovidReport.this, TestInfectionActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        coronaHospitals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CovidReport.this, CoronaHospitalsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        helpLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CovidReport.this, HelplineActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}
