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

import com.shoutlab.coronabd.CovidArticle.ArticleAdapter;
import com.shoutlab.coronabd.CovidArticle.ArticleItem;
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

public class MaskActivity extends AppCompatActivity {

    private AlertDialog alertDialog;
    private RecyclerView relatedVideo;
    private VideoAdapter videoAdapter;
    private ArrayList<VideoItems> videoItems = new ArrayList<>();
    private PreferenceManager preferenceManager;
    private RecyclerView maskUseRecycler;
    private ArticleAdapter maskUseAdapter;
    private ArrayList<ArticleItem> maskUseList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mask);

        preferenceManager = new PreferenceManager(MaskActivity.this);

        ImageView backButton = findViewById(R.id.back_button);
        BlurView blurView = findViewById(R.id.blurView);
        relatedVideo = findViewById(R.id.related_video);
        maskUseRecycler = findViewById(R.id.mask_uses);

        float radius = 20f;

        ViewGroup rootView = findViewById(R.id.root);
        Drawable windowBackground = getWindow().getDecorView().getBackground();

        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(true);

        LinearLayoutManager videoLayoutManager = new LinearLayoutManager(MaskActivity.this, LinearLayoutManager.HORIZONTAL, false);
        relatedVideo.setLayoutManager(videoLayoutManager);
        videoAdapter = new VideoAdapter(MaskActivity.this, videoItems);

        LinearLayoutManager maskUseLayoutManager = new LinearLayoutManager(MaskActivity.this);
        maskUseRecycler.setLayoutManager(maskUseLayoutManager);
        maskUseAdapter = new ArticleAdapter(MaskActivity.this, maskUseList);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(MaskActivity.this, HygieneActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        loadMaskData();
    }

    private void loadMaskData() {
        showLoading();
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .maskData(preferenceManager.getUUID());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                String resp = null;
                try {
                    if (response.body() != null) {
                        resp = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(resp);

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

                            JSONArray maskList = jsonObject.getJSONArray("maskList");
                            for(int i = 0; i < maskList.length(); i++) {
                                JSONObject actor = maskList.getJSONObject(i);
                                maskUseList.add(new ArticleItem(
                                        "How to use mask",
                                        actor.getString("image"),
                                        actor.getString("image")));
                            }
                            maskUseRecycler.setAdapter(maskUseAdapter);

                            dismissLoading();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        dismissLoading();
                        Toast.makeText(MaskActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    dismissLoading();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                dismissLoading();
                Toast.makeText(MaskActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MaskActivity.this);
        View view;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view = LayoutInflater.from(MaskActivity.this).inflate(R.layout.popup_loading, null);
        } else {
            view = LayoutInflater.from(MaskActivity.this).inflate(R.layout.popup_loading_old, null);
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
//        Intent intent = new Intent(MaskActivity.this, HygieneActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}
