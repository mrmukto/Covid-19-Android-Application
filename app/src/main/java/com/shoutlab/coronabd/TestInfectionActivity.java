package com.shoutlab.coronabd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.shoutlab.coronabd.Retrofit.RetrofitClient;
import com.shoutlab.coronabd.TestInfection.TestAdapter;
import com.shoutlab.coronabd.TestInfection.TestItems;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestInfectionActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private TestAdapter testAdapter;
    private ArrayList<TestItems> testItems = new ArrayList<>();
    private AlertDialog alertDialog;
    private RecyclerView testQuesRecycler;
    public static ArrayList<String> answerList = new ArrayList<>();
    public static ArrayList<String> progressCount = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    public static RoundCornerProgressBar progressBar;
    @SuppressLint("StaticFieldLeak")
    public static EditText ageValue;
    public static CardView checkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_infection);

        answerList = new ArrayList<>();
        progressCount = new ArrayList<>();
        preferenceManager = new PreferenceManager(TestInfectionActivity.this);

        ImageView backButton = findViewById(R.id.back_button);
        BlurView blurView = findViewById(R.id.blurView);
        testQuesRecycler = findViewById(R.id.testQuesRecycler);
        progressBar = findViewById(R.id.progressMeter);
        ageValue = findViewById(R.id.userAge);
        checkButton = findViewById(R.id.checkResult);

        progressBar.setProgress(progressCount.size());

        float radius = 20f;

        ViewGroup rootView = findViewById(R.id.root);
        Drawable windowBackground = getWindow().getDecorView().getBackground();

        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(true);

        LinearLayoutManager quesSlideLayoutManager = new LinearLayoutManager(TestInfectionActivity.this, LinearLayoutManager.HORIZONTAL, false);
        testQuesRecycler.setLayoutManager(quesSlideLayoutManager);
        SnapHelper snapHelper = new GravitySnapHelper(Gravity.CENTER);
        snapHelper.attachToRecyclerView(testQuesRecycler);
        testAdapter = new TestAdapter(TestInfectionActivity.this, testItems);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!ageValue.getText().toString().equals("")){
                    if(progressCount.size() == testItems.size()){
                        calculateResult();
                    } else {
                        Toast.makeText(TestInfectionActivity.this, "সবগুলো প্রশ্নের উত্তর নির্বাচন করুন", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TestInfectionActivity.this, "আপনার বয়স উল্লেখ করুন", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadTestQuestion();
    }

    private void calculateResult() {
        showLoading();
        String answerString = "0";

        for (String s : answerList)
        {
            answerString = String.format("%s_%s", answerString, s);
        }

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .checkInfection(preferenceManager.getUUID(), ageValue.getText().toString(), answerString);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                String resp = null;
                try {
                    if (response.body() != null) {
                        resp = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(resp);
                            String checkResult = jsonObject.getString("result");
                            String checkResultBN = jsonObject.getString("result_bn");
                            Intent intent = new Intent(TestInfectionActivity.this, CovidReport.class);
                            intent.putExtra("RESULT", checkResult);
                            intent.putExtra("RESULT_BN", checkResultBN);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            dismissLoading();
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        dismissLoading();
                        Toast.makeText(TestInfectionActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    dismissLoading();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                dismissLoading();
                Toast.makeText(TestInfectionActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTestQuestion() {
        showLoading();
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .getTestQuestion(preferenceManager.getUUID());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                String resp = null;
                try {
                    if (response.body() != null) {
                        resp = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(resp);

                            JSONArray questionList = jsonObject.getJSONArray("questionList");
                            for(int i = 0; i < questionList.length(); i++) {
                                JSONObject actor = questionList.getJSONObject(i);
                                testItems.add(new TestItems(
                                        actor.getString("quesID"),
                                        actor.getString("question"),
                                        actor.getString("image")
                                ));
                            }
                            progressBar.setMax(testItems.size());
                            testQuesRecycler.setAdapter(testAdapter);

                            dismissLoading();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        dismissLoading();
                        Toast.makeText(TestInfectionActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    dismissLoading();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                dismissLoading();
                Toast.makeText(TestInfectionActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
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
        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(TestInfectionActivity.this);
        View view;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view = LayoutInflater.from(TestInfectionActivity.this).inflate(R.layout.popup_loading, null);
        } else {
            view = LayoutInflater.from(TestInfectionActivity.this).inflate(R.layout.popup_loading_old, null);
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
