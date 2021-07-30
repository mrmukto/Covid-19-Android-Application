package com.shoutlab.coronabd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import com.shoutlab.coronabd.RelatedVideo.VideoAdapter;
import com.shoutlab.coronabd.RelatedVideo.VideoItems;
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

public class HandwashActivity extends AppCompatActivity {

    private AlertDialog alertDialog;
    private RecyclerView relatedVideo;
    private VideoAdapter videoAdapter;
    private ArrayList<VideoItems> videoItems = new ArrayList<>();
    private PreferenceManager preferenceManager;
    private ViewGroup rootView;
    private ImageView handWashStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handwash);

        preferenceManager = new PreferenceManager(HandwashActivity.this);

        handWashStep = findViewById(R.id.handWash);
        ImageView backButton = findViewById(R.id.back_button);
        BlurView blurView = findViewById(R.id.blurView);
        relatedVideo = findViewById(R.id.related_video);

        float radius = 20f;

        rootView = findViewById(R.id.root);
        Drawable windowBackground = getWindow().getDecorView().getBackground();

        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(true);

        LinearLayoutManager videoLayoutManager = new LinearLayoutManager(HandwashActivity.this, LinearLayoutManager.HORIZONTAL, false);
        relatedVideo.setLayoutManager(videoLayoutManager);
        videoAdapter = new VideoAdapter(HandwashActivity.this, videoItems);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(HandwashActivity.this, HygieneActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        loadHandWashData();
    }

    private void loadHandWashData() {
        showLoading();
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .handWashData(preferenceManager.getUUID());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                String resp = null;
                try {
                    if (response.body() != null) {
                        resp = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(resp);

                            Glide.with(rootView)
                                    .load(jsonObject.getString("image"))
                                    .into(handWashStep);

                            JSONArray videoList = jsonObject.getJSONArray("relatedVideo");
                            for(int i = 0; i < videoList.length(); i++) {
                                JSONObject actor = videoList.getJSONObject(i);
                                videoItems.add(new VideoItems(
                                        actor.getString("thumbnail"),
                                        actor.getString("title"),
                                        actor.getString("details"),
                                        actor.getString("url")));
                            }
                            relatedVideo.setAdapter(videoAdapter);

                            dismissLoading();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        dismissLoading();
                        Toast.makeText(HandwashActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    dismissLoading();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                dismissLoading();
                Toast.makeText(HandwashActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HandwashActivity.this);
        View view;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view = LayoutInflater.from(HandwashActivity.this).inflate(R.layout.popup_loading, null);
        } else {
            view = LayoutInflater.from(HandwashActivity.this).inflate(R.layout.popup_loading_old, null);
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
//        Intent intent = new Intent(HandwashActivity.this, HygieneActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}
