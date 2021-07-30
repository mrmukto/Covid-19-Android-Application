package com.shoutlab.coronabd;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonObject;
import com.shoutlab.coronabd.HomeSlider.SliderAdapter;
import com.shoutlab.coronabd.HomeSlider.SliderItems;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private static final int LOCATION_REQUEST_CODE = 999;
    private ArrayList<SliderItems> sliderItems = new ArrayList<>();
    private ArrayList<VideoItems> videoItems = new ArrayList<>();
    private boolean doubleBackToExitPressedOnce = false;
    private LatLng latLng;
    private AlertDialog alertDialog;
    private PreferenceManager preferenceManager;
    private CardView emergencyCard;
    private TextView emergencyNumber;
    private RecyclerView topSlider;
    private SliderAdapter sliderAdapter;
    private VideoAdapter videoAdapter;
    private RecyclerView relatedVideo;
    private RoundCornerProgressBar cured;
    private RoundCornerProgressBar death;
    private TextView deathNumber;
    private CardView contacttracingview;
    private TextView infectedNumber;
    private TextView quarantineDone, covidnegative, quarantineTotal, lastInfected, totalInfected, totalCured, totalDeath, totalIsolation, updateTime, updateTimeEn;
    private String coronaNewsStr = "https://www.who.int/bangladesh/news/releases/";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        preferenceManager = new PreferenceManager(HomeActivity.this);


        contacttracingview = findViewById(R.id.contacttracingview);
        CardView faqButton = findViewById(R.id.faq_button);
        CardView covid19Button = findViewById(R.id.covid19_button);
        CardView mythButton = findViewById(R.id.myth_button);
        CardView hygieneButton = findViewById(R.id.hygieneButton);
        CardView hospitalButton = findViewById(R.id.hospital_button);
        CardView mentalHealth = findViewById(R.id.mentalHealth);
        CardView quarantineIsolation = findViewById(R.id.quarantineIsolation);
        CardView testInfection = findViewById(R.id.testInfection);
        topSlider = findViewById(R.id.top_slider);
        relatedVideo = findViewById(R.id.related_video);
        BlurView blurView = findViewById(R.id.blurView);
        ImageView emergencyCall = findViewById(R.id.emergency_call);
        ImageView volunteerButton = findViewById(R.id.volunteerButton);
        ImageView donateButton = findViewById(R.id.donateButton);
        TextView helplineList = findViewById(R.id.helplineList);
        ImageView coronaNews = findViewById(R.id.coronaNews);
        ImageView about = findViewById(R.id.about);
        emergencyCard = findViewById(R.id.emergency_card);
        emergencyNumber = findViewById(R.id.emergency_number);
        cured = findViewById(R.id.curedMeter);
        death = findViewById(R.id.deathMeter);
        RoundCornerProgressBar infected = findViewById(R.id.infectedMeter);
        deathNumber = findViewById(R.id.deathNumber);
        infectedNumber = findViewById(R.id.infectedNumber);
        TextView userName = findViewById(R.id.userName);
        quarantineDone = findViewById(R.id.quarantineDone1);
        quarantineTotal = findViewById(R.id.totalQuarantine1);
        lastInfected = findViewById(R.id.lastInfected);
        totalInfected = findViewById(R.id.totalInfected);
        totalCured = findViewById(R.id.totalCured);
        totalDeath = findViewById(R.id.totalDeath);
        totalIsolation = findViewById(R.id.totalIsolation);
        updateTime = findViewById(R.id.updateTime);
        ImageView bdStatDetails = findViewById(R.id.bdStatDetails);
        updateTimeEn = findViewById(R.id.updateTimeEN);
        View situationReport = findViewById(R.id.situationReport);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        covidnegative = findViewById(R.id.covidnegativebutton);

        if (Boolean.parseBoolean(preferenceManager.getStatus().toLowerCase())) {
            covidnegative.setText("Covid Positive");
            covidnegative.setTextColor(Color.RED);
        } else {
            covidnegative.setText("Covid Negative");
            covidnegative.setTextColor(Color.GREEN);
        }

        covidnegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ContactTracing.class));
            }
        });


        if (Long.parseLong(preferenceManager.getRegisterTime()) + 14 * 24 * 60 * 60 * 1000 < System.currentTimeMillis()) {
            preferenceManager.removeAll();
            Intent i = new Intent(this, CreateProfileActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }


        OkHttpClient client1 = new OkHttpClient();

        Request request1 = new Request.Builder()
                .url("https://worldometers.p.rapidapi.com/api/coronavirus/world")
                .get()
                .addHeader("x-rapidapi-key", "fdbc8e6058mshc65f139fb4fb863p126f66jsn1c112595feb3")
                .addHeader("x-rapidapi-host", "worldometers.p.rapidapi.com")
                .build();


        client1.newCall(request1).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                String responsee = response.body().string();
                Log.e("responseeee", responsee);
                try {
                    JSONObject jsonObject = new JSONObject(responsee);
                    Log.e("responseeee", jsonObject.toString());
                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                    Log.e("data",jsonObject1.toString());
                    String totaldeath = jsonObject1.getString("Total Deaths");
                    String totalinfected = jsonObject1.getString("Total Cases");

                    Log.e("totaldeath2",totaldeath);
                    Log.e("totalinfected2",totalinfected);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            deathNumber.setText(totaldeath);
                            infectedNumber.setText(totalinfected);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });


        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://worldometers.p.rapidapi.com/api/coronavirus/country/Bangladesh")
                .get()
                .addHeader("x-rapidapi-key", "fdbc8e6058mshc65f139fb4fb863p126f66jsn1c112595feb3")
                .addHeader("x-rapidapi-host", "worldometers.p.rapidapi.com")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.e("failed", e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                String s = response.body().string();
                Log.e("response", s);
                try {
                    JSONObject tempjson = new JSONObject(s);
                    JSONObject resultjson = tempjson.getJSONObject("data");
                    String active_case = resultjson.getString("Active Cases");
                    String new_case = resultjson.getString("New Cases");
                    String new_deaths = resultjson.getString("New Deaths");
                    String new_recovered = resultjson.getString("New Recovered");
                    String total_case = resultjson.getString("Total Cases");
                    String total_deaths = resultjson.getString("Total Deaths");
                    String total_recovered = resultjson.getString("Total Recovered");
                    String total_tests = resultjson.getString("Total Tests");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            quarantineTotal.setText(total_case);
                            Log.e("totalcase", total_case);
                            quarantineDone.setText(new_case);
                            Log.e("newcase", new_case);
                            lastInfected.setText(new_deaths);
                            Log.e("newdeath", new_deaths);
                            totalInfected.setText(new_recovered);
                            Log.e("newrecovered", new_recovered);

                            totalCured.setText(total_recovered);
                            Log.e("totalrecovered", total_recovered);
                            totalDeath.setText(total_deaths);
                            Log.e("totaldeath", total_deaths);
                            totalIsolation.setText(total_tests);
                            Log.e("totalisolation", total_tests);


                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sliderItems = new ArrayList<>();
                videoItems = new ArrayList<>();
                getHomeData();
            }
        });

        userName.setText(String.format("%s!", preferenceManager.getName()));
        infected.setProgress(100);

        float radius = 20f;

        ViewGroup rootView = findViewById(R.id.root);
        Drawable windowBackground = getWindow().getDecorView().getBackground();

        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(true);

        LinearLayoutManager sliderLayoutManager = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false);
        topSlider.setLayoutManager(sliderLayoutManager);
        sliderAdapter = new SliderAdapter(HomeActivity.this, sliderItems);

        LinearLayoutManager videoLayoutManager = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false);
        relatedVideo.setLayoutManager(videoLayoutManager);
        videoAdapter = new VideoAdapter(HomeActivity.this, videoItems);

        emergencyCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCallConfirmation(preferenceManager.getCountry(), emergencyNumber.getText().toString());
            }
        });

        faqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, FaqActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                finish();
            }
        });

        covid19Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, Covid19Activity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                finish();
            }
        });

        mythButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MythsActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                finish();
            }
        });

        hygieneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, HygieneActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                finish();
            }
        });

        hospitalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHospitalConfirmation();
            }
        });

        volunteerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, VolunteerActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                finish();
            }
        });

        donateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, DonateActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                finish();
            }
        });

        helplineList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, HelplineActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                finish();
            }
        });

        bdStatDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://covid19tracker.gov.bd/"));
                startActivity(browserIntent);
            }
        });

        situationReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, SituationActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                finish();
            }
        });

        mentalHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MentalHealthActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                finish();
            }
        });

        quarantineIsolation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, QuarantineIsolationActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                finish();
            }
        });


        contacttracingview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ContactTracing.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                finish();
            }
        });

        testInfection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, TestInfectionActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                finish();
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CreditActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                finish();
            }
        });

        coronaNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(coronaNewsStr));
                startActivity(browserIntent);
            }
        });

        getHomeData();
    }

    private void getHomeData() {
        showLoading();
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .homeData(preferenceManager.getUUID(), preferenceManager.getCountry());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                dismissLoading();
                String resp = null;
                try {
                    if (response.body() != null) {
                        resp = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(resp);
                            boolean error = jsonObject.getBoolean("error");
                            if (!error) {
                                if (jsonObject.getBoolean("showEmergency")) {
                                    emergencyNumber.setText(jsonObject.getString("emergencyNumber"));
                                } else {
                                    emergencyCard.setVisibility(View.GONE);
                                }

                                coronaNewsStr = jsonObject.getString("coronaNews");

                                JSONArray topSliderArray = jsonObject.getJSONArray("topSliders");
                                for (int i = 0; i < topSliderArray.length(); i++) {
                                    JSONObject actor = topSliderArray.getJSONObject(i);
                                    sliderItems.add(new SliderItems(actor.getString("slide")));
                                }
                                topSlider.setAdapter(sliderAdapter);

                                JSONArray videoArray = jsonObject.getJSONArray("relatedVideo");
                                for (int i = 0; i < videoArray.length(); i++) {
                                    JSONObject actor = videoArray.getJSONObject(i);
                                    videoItems.add(new VideoItems(
                                            actor.getString("thumbnail"),
                                            actor.getString("title"),
                                            actor.getString("details"),
                                            actor.getString("url")));
                                }
                                relatedVideo.setAdapter(videoAdapter);


                                float deathProgress = (Float.parseFloat(jsonObject.getString("death").replace(",", "")) / Float.parseFloat(jsonObject.getString("confirmed").replace(",", ""))) * 100;
                                death.setProgress(deathProgress);

//                                quarantineTotal.setText(jsonObject.getString("totalQuarantine"));
//                                quarantineDone.setText(jsonObject.getString("quarantineDone"));
//                                lastInfected.setText(jsonObject.getString("lastInfected"));
//                                totalInfected.setText(jsonObject.getString("totalInfected"));
//                                totalCured.setText(jsonObject.getString("totalCured"));
//                                totalDeath.setText(jsonObject.getString("totalDeath"));
//                                totalIsolation.setText(jsonObject.getString("totalIsolation"));
                                updateTime.setText(jsonObject.getString("bn_date"));
                                updateTimeEn.setText(jsonObject.getString("en_date"));

                                swipeRefreshLayout.setRefreshing(false);
                                dismissLoading();
                            } else {
                                swipeRefreshLayout.setRefreshing(false);
                                Toast.makeText(HomeActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(HomeActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }

                    Log.e("response", resp);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("InflateParams")
    private void showCallConfirmation(String country, String number) {
        // Get screen width and height in pixels
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // The absolute width of the available display size in pixels.
        int displayWidth = displayMetrics.widthPixels;
        // Initialize a new window manager layout parameters
        // Set alert dialog width equal to screen width 70%
        int dialogWindowWidth = (int) (displayWidth * 0.8f);
        // Set alert dialog height equal to screen height 70%
        int dialogWindowHeight = (int) (displayWidth * 0.9f);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(HomeActivity.this);
        View view;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.popup_call_emergency, null);
        } else {
            view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.popup_call_emergency_old, null);
        }
        TextView title = view.findViewById(R.id.title);
        TextView emergencyNumber = view.findViewById(R.id.emergency_number);
        title.setText(String.format("%s Emergency Number", country));
        emergencyNumber.setText(number);
        TextView cancel = view.findViewById(R.id.cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        CardView emergency_call = view.findViewById(R.id.emergency_call);
        emergency_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + number));
                startActivity(intent);
                alertDialog.dismiss();
            }
        });

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
    private void showHospitalConfirmation() {
        // Get screen width and height in pixels
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // The absolute width of the available display size in pixels.
        int displayWidth = displayMetrics.widthPixels;
        // Initialize a new window manager layout parameters
        // Set alert dialog width equal to screen width 70%
        int dialogWindowWidth = (int) (displayWidth * 0.8f);
        // Set alert dialog height equal to screen height 70%
        int dialogWindowHeight = (int) (displayWidth * 1.0f);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(HomeActivity.this);
        View view;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.popup_show_hospital, null);
        } else {
            view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.popup_show_hospital_old, null);
        }

        ImageView cancel = view.findViewById(R.id.close_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        CardView coronaHospital = view.findViewById(R.id.coronaHospitals);
        coronaHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CoronaHospitalsActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                alertDialog.dismiss();
            }
        });

        CardView nearbyHospital = view.findViewById(R.id.nearbyHospital);
        nearbyHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                showLoading();
                showNearbyHospital();
            }
        });

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

    private void showNearbyHospital() {
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            dismissLoading();
            requestPermission();
        } else {
            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(HomeActivity.this);
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(HomeActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        dismissLoading();
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        Intent intent = new Intent(HomeActivity.this, HospitalActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("latitude", String.valueOf(latLng.latitude));
                        intent.putExtra("longitude", String.valueOf(latLng.longitude));
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                        finish();
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        showNearbyHospital();
                    }
                }
            });
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showLoading();
                showNearbyHospital();
            }
        }
    }

    @SuppressLint("InflateParams")
    private void showLoading() {
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
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
        View view;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.popup_loading, null);
        } else {
            view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.popup_loading_old, null);
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

    private void dismissLoading() {
        alertDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(HomeActivity.this, "Please press BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
