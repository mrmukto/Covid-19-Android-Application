package com.shoutlab.coronabd;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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

public class Covid19Activity extends AppCompatActivity {

    private ArrayList<ArticleItem> articleItems = new ArrayList<>();
    private ArrayList<SliderItems> sliderItems = new ArrayList<>();
    private ArrayList<SliderItems> severitySliderItems = new ArrayList<>();
    private ArrayList<SliderItems> workplaceItems = new ArrayList<>();
    private ArticleAdapter articleAdapter;
    private GeneralSliderAdapter generalSliderAdapter;
    private GeneralSliderAdapter severitySliderAdapter;
    private GeneralSliderAdapter workplaceSliderAdapter;
    private RecyclerView readySlide;
    private RecyclerView severityRecycler;
    private RecyclerView workplaceRecycler;
    private PreferenceManager preferenceManager;
    private AlertDialog alertDialog;
    private RecyclerView articleRecycler;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid19);

        preferenceManager = new PreferenceManager(Covid19Activity.this);

        ImageView back_button = findViewById(R.id.back_button);
        articleRecycler = findViewById(R.id.articleList);
        readySlide = findViewById(R.id.readySlide);
        BlurView blurView = findViewById(R.id.blurView);
        severityRecycler = findViewById(R.id.severitySlide);
        workplaceRecycler = findViewById(R.id.workplaceSlide);

        float radius = 20f;

        ViewGroup rootView = findViewById(R.id.root);
        Drawable windowBackground = getWindow().getDecorView().getBackground();

        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(true);

        LinearLayoutManager articleLayoutManager = new LinearLayoutManager(Covid19Activity.this);
        articleRecycler.setLayoutManager(articleLayoutManager);
        articleAdapter = new ArticleAdapter(Covid19Activity.this, articleItems);

        LinearLayoutManager readySlideLayoutManager = new LinearLayoutManager(Covid19Activity.this, LinearLayoutManager.HORIZONTAL, false);
        readySlide.setLayoutManager(readySlideLayoutManager);
        generalSliderAdapter = new GeneralSliderAdapter(Covid19Activity.this, sliderItems);

        LinearLayoutManager severitySlideLayoutManager = new LinearLayoutManager(Covid19Activity.this, LinearLayoutManager.HORIZONTAL, false);
        severityRecycler.setLayoutManager(severitySlideLayoutManager);
        severitySliderAdapter = new GeneralSliderAdapter(Covid19Activity.this, severitySliderItems);

        LinearLayoutManager workplaceSlideLayoutManager = new LinearLayoutManager(Covid19Activity.this, LinearLayoutManager.HORIZONTAL, false);
        workplaceRecycler.setLayoutManager(workplaceSlideLayoutManager);
        workplaceSliderAdapter = new GeneralSliderAdapter(Covid19Activity.this, workplaceItems);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(Covid19Activity.this, HomeActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        loadData();
    }

    private void loadData() {
        showLoading();
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .covidData(preferenceManager.getUUID());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                String resp = null;
                try {
                    if (response.body() != null) {
                        resp = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(resp);

                            JSONArray articleList = jsonObject.getJSONArray("articleList");
                            for(int i = 0; i < articleList.length(); i++) {
                                JSONObject actor = articleList.getJSONObject(i);
                                articleItems.add(new ArticleItem(
                                        actor.getString("title"),
                                        actor.getString("image"),
                                        actor.getString("url")
                                ));
                            }
                            articleRecycler.setAdapter(articleAdapter);

                            JSONArray readySlider = jsonObject.getJSONArray("readySlider");
                            for(int i = 0; i < readySlider.length(); i++) {
                                JSONObject actor = readySlider.getJSONObject(i);
                                sliderItems.add(new SliderItems(actor.getString("image")));
                            }
                            readySlide.setAdapter(generalSliderAdapter);

                            JSONArray severitySlider = jsonObject.getJSONArray("severitySlider");
                            for(int i = 0; i < severitySlider.length(); i++) {
                                JSONObject actor = severitySlider.getJSONObject(i);
                                severitySliderItems.add(new SliderItems(actor.getString("image")));
                            }
                            severityRecycler.setAdapter(severitySliderAdapter);

                            JSONArray workplaceSlider = jsonObject.getJSONArray("workplaceSlider");
                            for(int i = 0; i < workplaceSlider.length(); i++) {
                                JSONObject actor = workplaceSlider.getJSONObject(i);
                                workplaceItems.add(new SliderItems(actor.getString("image")));
                            }
                            workplaceRecycler.setAdapter(workplaceSliderAdapter);

                            dismissLoading();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        dismissLoading();
                        Toast.makeText(Covid19Activity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    dismissLoading();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                dismissLoading();
                Toast.makeText(Covid19Activity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Covid19Activity.this);
        View view;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view = LayoutInflater.from(Covid19Activity.this).inflate(R.layout.popup_loading, null);
        } else {
            view = LayoutInflater.from(Covid19Activity.this).inflate(R.layout.popup_loading_old, null);
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
//        Intent intent = new Intent(Covid19Activity.this, HomeActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}
