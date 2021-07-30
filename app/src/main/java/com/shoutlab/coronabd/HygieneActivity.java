package com.shoutlab.coronabd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import com.shoutlab.coronabd.CovidArticle.ArticleAdapter;
import com.shoutlab.coronabd.CovidArticle.ArticleItem;
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

public class HygieneActivity extends AppCompatActivity {

    private AlertDialog alertDialog;
    private PreferenceManager preferenceManager;
    private RecyclerView travelSlider;
    private RecyclerView protectSlider;
    private RecyclerView socialSlider;
    private RecyclerView ncdRecycler;
    private GeneralSliderAdapter travelSliderAdapter;
    private GeneralSliderAdapter protectSliderAdapter;
    private GeneralSliderAdapter ncdSliderAdapter;
    private ArticleAdapter socialSliderAdapter;
    private ArrayList<SliderItems> travelSliderItem = new ArrayList<>();
    private ArrayList<SliderItems> protectSliderItem = new ArrayList<>();
    private ArrayList<ArticleItem> socialSliderItem = new ArrayList<>();
    private ArrayList<SliderItems> ncdItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hygiene);

        preferenceManager = new PreferenceManager(HygieneActivity.this);
        ImageView back_button = findViewById(R.id.back_button);
        CardView maskButton = findViewById(R.id.maskButton);
        CardView handWashButton = findViewById(R.id.handWashButton);
        travelSlider = findViewById(R.id.travelSlider);
        protectSlider = findViewById(R.id.protectSlider);
        socialSlider = findViewById(R.id.socialSlider);
        ncdRecycler = findViewById(R.id.ncdRecycler);

        BlurView blurView = findViewById(R.id.blurView);

        float radius = 20f;

        ViewGroup rootView = findViewById(R.id.root);
        Drawable windowBackground = getWindow().getDecorView().getBackground();

        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(true);

        LinearLayoutManager protectSlideLayoutManager = new LinearLayoutManager(HygieneActivity.this, LinearLayoutManager.HORIZONTAL, false);
        protectSlider.setLayoutManager(protectSlideLayoutManager);
//        SnapHelper snapHelper = new GravitySnapHelper(Gravity.CENTER);
//        snapHelper.attachToRecyclerView(protectSlider);
        protectSliderAdapter = new GeneralSliderAdapter(HygieneActivity.this, protectSliderItem);

        LinearLayoutManager travelSlideLayoutManager = new LinearLayoutManager(HygieneActivity.this, LinearLayoutManager.HORIZONTAL, false);
        travelSlider.setLayoutManager(travelSlideLayoutManager);
        travelSliderAdapter = new GeneralSliderAdapter(HygieneActivity.this, travelSliderItem);

        LinearLayoutManager ncdSlideLayoutManager = new LinearLayoutManager(HygieneActivity.this, LinearLayoutManager.HORIZONTAL, false);
        ncdRecycler.setLayoutManager(ncdSlideLayoutManager);
        ncdSliderAdapter = new GeneralSliderAdapter(HygieneActivity.this, ncdItems);

        LinearLayoutManager socialSliderLayoutManager = new LinearLayoutManager(HygieneActivity.this);
        socialSlider.setLayoutManager(socialSliderLayoutManager);
        socialSliderAdapter = new ArticleAdapter(HygieneActivity.this, socialSliderItem);

        maskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HygieneActivity.this, MaskActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                finish();
            }
        });

        handWashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HygieneActivity.this, HandwashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                finish();
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(HygieneActivity.this, HomeActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        loadHygieneData();
    }

    private void loadHygieneData() {
        showLoading();
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .getHygiene(preferenceManager.getUUID());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                String resp = null;
                try {
                    if (response.body() != null) {
                        resp = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(resp);

                            JSONArray travelList = jsonObject.getJSONArray("travelList");
                            for(int i = 0; i < travelList.length(); i++) {
                                JSONObject actor = travelList.getJSONObject(i);
                                travelSliderItem.add(new SliderItems(actor.getString("image")));
                            }
                            travelSlider.setAdapter(travelSliderAdapter);

                            JSONArray protectList = jsonObject.getJSONArray("protectList");
                            for(int i = 0; i < protectList.length(); i++) {
                                JSONObject actor = protectList.getJSONObject(i);
                                protectSliderItem.add(new SliderItems(actor.getString("image")));
                            }
                            protectSlider.setAdapter(protectSliderAdapter);

                            JSONArray ncdList = jsonObject.getJSONArray("ncdList");
                            for(int i = 0; i < ncdList.length(); i++) {
                                JSONObject actor = ncdList.getJSONObject(i);
                                ncdItems.add(new SliderItems(actor.getString("image")));
                            }
                            ncdRecycler.setAdapter(ncdSliderAdapter);

                            JSONArray socialList = jsonObject.getJSONArray("socialList");
                            for(int i = 0; i < socialList.length(); i++) {
                                JSONObject actor = socialList.getJSONObject(i);
                                socialSliderItem.add(new ArticleItem(
                                        "Social Distance",
                                        actor.getString("image"),
                                        actor.getString("image")));
                            }
                            socialSlider.setAdapter(socialSliderAdapter);

                            dismissLoading();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        dismissLoading();
                        Toast.makeText(HygieneActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    dismissLoading();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                dismissLoading();
                Toast.makeText(HygieneActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HygieneActivity.this);
        View view;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view = LayoutInflater.from(HygieneActivity.this).inflate(R.layout.popup_loading, null);
        } else {
            view = LayoutInflater.from(HygieneActivity.this).inflate(R.layout.popup_loading_old, null);
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
//        Intent intent = new Intent(HygieneActivity.this, HomeActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}
