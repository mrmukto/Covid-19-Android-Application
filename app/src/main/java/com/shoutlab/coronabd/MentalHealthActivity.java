package com.shoutlab.coronabd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.shoutlab.coronabd.HomeSlider.GeneralSliderAdapter;
import com.shoutlab.coronabd.HomeSlider.SliderItems;
import com.shoutlab.coronabd.Retrofit.RetrofitClient;

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

public class MentalHealthActivity extends AppCompatActivity {

    private GeneralSliderAdapter healthSliderAdapter;
    private ArrayList<SliderItems> healthItems = new ArrayList<>();
    private PreferenceManager preferenceManager;
    private AlertDialog alertDialog;
    private RecyclerView healthRecycler;
    private ImageView mentalHealthChild, mentalHealthMatured;
    private ViewGroup rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mental_health);

        preferenceManager = new PreferenceManager(MentalHealthActivity.this);

        healthRecycler = findViewById(R.id.healthSlider);
        mentalHealthChild = findViewById(R.id.mentalHealthChild);
        mentalHealthMatured = findViewById(R.id.mentalHealthMatured);
        ImageView backButton = findViewById(R.id.back_button);

        BlurView blurView = findViewById(R.id.blurView);

        float radius = 20f;

        rootView = findViewById(R.id.root);
        Drawable windowBackground = getWindow().getDecorView().getBackground();

        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(true);

        LinearLayoutManager healthSlideLayoutManager = new LinearLayoutManager(MentalHealthActivity.this, LinearLayoutManager.HORIZONTAL, false);
        healthRecycler.setLayoutManager(healthSlideLayoutManager);
        healthSliderAdapter = new GeneralSliderAdapter(MentalHealthActivity.this, healthItems);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(MentalHealthActivity.this, HomeActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        loadHealthData();
    }

    private void loadHealthData() {
        showLoading();
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .mentalHealth(preferenceManager.getUUID());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                String resp = null;
                try {
                    if (response.body() != null) {
                        resp = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(resp);

                            String childrenURL = jsonObject.getString("stressChildren");
                            Glide.with(rootView)
                                    .load(childrenURL)
                                    .into(mentalHealthChild);

                            String maturedURL = jsonObject.getString("stressMatured");
                            Glide.with(rootView)
                                    .load(maturedURL)
                                    .into(mentalHealthMatured);

                            mentalHealthChild.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(childrenURL));
                                    startActivity(browserIntent);
                                }
                            });

                            mentalHealthMatured.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(maturedURL));
                                    startActivity(browserIntent);
                                }
                            });

                            JSONArray healthSeries = jsonObject.getJSONArray("healthSeries");
                            for(int i = 0; i < healthSeries.length(); i++) {
                                JSONObject actor = healthSeries.getJSONObject(i);
                                healthItems.add(new SliderItems(actor.getString("image")));
                            }
                            healthRecycler.setAdapter(healthSliderAdapter);
                            dismissLoading();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        dismissLoading();
                        Toast.makeText(MentalHealthActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    dismissLoading();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                dismissLoading();
                Toast.makeText(MentalHealthActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MentalHealthActivity.this);
        View view;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view = LayoutInflater.from(MentalHealthActivity.this).inflate(R.layout.popup_loading, null);
        } else {
            view = LayoutInflater.from(MentalHealthActivity.this).inflate(R.layout.popup_loading_old, null);
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
//        Intent intent = new Intent(MentalHealthActivity.this, HomeActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}
