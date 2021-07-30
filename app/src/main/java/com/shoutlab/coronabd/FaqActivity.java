package com.shoutlab.coronabd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shoutlab.coronabd.Faq.FaqAdapter;
import com.shoutlab.coronabd.Faq.FaqItem;
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

public class FaqActivity extends AppCompatActivity {

    private RecyclerView faqRecycler;
    private FaqAdapter faqAdapter;
    private AlertDialog alertDialog;
    private TextView notFound;

    private ArrayList<FaqItem> faqItems = new ArrayList<>();
    private ArrayList<VideoItems> videoItems = new ArrayList<>();
    private PreferenceManager preferenceManager;
    private VideoAdapter videoAdapter;
    private RecyclerView relatedVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        preferenceManager = new PreferenceManager(FaqActivity.this);

        faqRecycler = findViewById(R.id.faqList);
        notFound = findViewById(R.id.notFound);
        ImageView back_button = findViewById(R.id.back_button);
        relatedVideo = findViewById(R.id.related_video);
        BlurView blurView = findViewById(R.id.blurView);
        EditText searchFaq = findViewById(R.id.searchCovid);

        float radius = 20f;

        ViewGroup rootView = findViewById(R.id.root);
        Drawable windowBackground = getWindow().getDecorView().getBackground();

        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(true);

        LinearLayoutManager faqLayoutManager = new LinearLayoutManager(FaqActivity.this);
        faqRecycler.setLayoutManager(faqLayoutManager);
        faqAdapter = new FaqAdapter(FaqActivity.this, faqItems);

        LinearLayoutManager videoLayoutManager = new LinearLayoutManager(FaqActivity.this, LinearLayoutManager.HORIZONTAL, false);
        relatedVideo.setLayoutManager(videoLayoutManager);
        videoAdapter = new VideoAdapter(FaqActivity.this, videoItems);

        searchFaq.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(FaqActivity.this, HomeActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        getFaqList();
    }

    private void getFaqList() {
        showLoading();
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .faqList(preferenceManager.getUUID());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                String resp = null;
                try {
                    if (response.body() != null) {
                        resp = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(resp);

                            JSONArray faqList = jsonObject.getJSONArray("faqList");
                            for(int i = 0; i < faqList.length(); i++) {
                                JSONObject actor = faqList.getJSONObject(i);
                                faqItems.add(new FaqItem(actor.getString("ques_en"), actor.getString("ans_en")));
                            }
                            faqRecycler.setAdapter(faqAdapter);

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
                        Toast.makeText(FaqActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    dismissLoading();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                dismissLoading();
                Toast.makeText(FaqActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent intent = new Intent(FaqActivity.this, HomeActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    private void filter(String text) {
        ArrayList<FaqItem> faqItemsFiltered = new ArrayList<>();

        for (FaqItem item : faqItems) {
            if (item.getQuestion().toLowerCase().contains(text.toLowerCase()) || item.getAnswer().toLowerCase().contains(text.toLowerCase())) {
                faqItemsFiltered.add(item);
            }
        }
        faqAdapter.filterList(faqItemsFiltered);

        if(faqItemsFiltered.size() == 0){
            notFound.setVisibility(View.VISIBLE);
        } else {
            notFound.setVisibility(View.GONE);
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
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FaqActivity.this);
        View view;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view = LayoutInflater.from(FaqActivity.this).inflate(R.layout.popup_loading, null);
        } else {
            view = LayoutInflater.from(FaqActivity.this).inflate(R.layout.popup_loading_old, null);
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
