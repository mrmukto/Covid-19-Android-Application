package com.shoutlab.coronabd;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class HospitalActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private String latitude, longitude;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);

        ImageView backButton = findViewById(R.id.back_button);
        showLoading();

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.nearbyHospital);

        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(HospitalActivity.this);
        }

        Intent intent = getIntent();
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");

        ImageView myLocation = findViewById(R.id.myLocation);

        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng currentLocation = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14));
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(HospitalActivity.this, HomeActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(HospitalActivity.this, R.raw.map_style));
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);

        LatLng currentLocation = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

        float zoomLevel = 16.0f;

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLocation);
        markerOptions.title("You are here");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.my_position));

        googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoomLevel));
        googleMap.getMaxZoomLevel();

        String URL = getNearbyPlacesURL(currentLocation);

        TaskRequestDirection taskRequestDirection = new TaskRequestDirection();
        taskRequestDirection.execute(URL);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private String getNearbyPlacesURL(LatLng currentLocation){
        double latitude = currentLocation.latitude;
        double longitude = currentLocation.longitude;

        return "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=" + latitude + "," + longitude +
                "&radius=10000" +
                "&type=hospital" +
                "&key=" + getString(R.string.google_places_key);
    }

    @SuppressLint("StaticFieldLeak")
    private class TaskRequestDirection extends AsyncTask<Object, String, String> {

        String googlePlacesData;
        String url;

        @Override
        protected String doInBackground(Object... objects) {
            url = (String)objects[0];
            try {
                googlePlacesData = downloadData(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return googlePlacesData;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            List<HashMap<String , String>> nearbyPlaceList = null;

            DataParser parser = new DataParser();
            nearbyPlaceList = parser.parse(s);

            showNearbyPlaces(nearbyPlaceList);
        }
    }

    private void showNearbyPlaces(List<HashMap<String, String>> nearbyPlaceList) {
        for(int i=0;i<nearbyPlaceList.size();i++)
        {
            //show all the places in the list
            //we are going to create marker options
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String,String> googlePlace = nearbyPlaceList.get(i);

            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            double latitude = Double.parseDouble(Objects.requireNonNull(googlePlace.get("latitude")));
            double longitude = Double.parseDouble(Objects.requireNonNull(googlePlace.get("longitude")));

            LatLng latLng = new LatLng(latitude, longitude);

            markerOptions.position(latLng);
            markerOptions.title(placeName + ", " + vicinity);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.hospital_pin));

            googleMap.addMarker(markerOptions);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(this.latitude), Double.parseDouble(this.longitude)), 12));
        }
        dismissLoading();
    }

    private String downloadData(String url) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL sourceUrl = new URL(url);

            urlConnection = (HttpURLConnection) sourceUrl.openConnection();
            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuilder sb = new StringBuilder();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            if (iStream != null) {
                iStream.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return data;
    }

    private static class DataParser {
        private HashMap<String , String> getPlace(JSONObject googlePlaceJson)
        {
            HashMap<String , String> googlePlacesMap = new HashMap<>();

            //store all the parameters using String
            String placeName = "-NA-";
            String vicinity = "-NA-";
            String latitude = "";
            String longitude = "";
            String reference = "";

            try {
                if (!googlePlaceJson.isNull("name")) {
                    placeName = googlePlaceJson.getString("name");
                }

                if (!googlePlaceJson.isNull("vicinity")) {
                    vicinity = googlePlaceJson.getString("vicinity");
                }

                latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
                longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");

                reference = googlePlaceJson.getString("reference");

                googlePlacesMap.put("place_name" , placeName);
                googlePlacesMap.put("vicinity" , vicinity);
                googlePlacesMap.put("latitude" , latitude);
                googlePlacesMap.put("longitude" , longitude);
                googlePlacesMap.put("reference" , reference);
            } catch(JSONException e) {
                e.printStackTrace();

            }
            return googlePlacesMap;
        }

        //to store all the places create a list of HashMap
        private List<HashMap<String,String>> getPlaces(JSONArray jsonArray)
        {
            //getPlace returns a HashMap for each place
            //getPlaces() creates a list of HashMaps
            int count = jsonArray.length();
            List<HashMap<String,String>> placesList = new ArrayList<>();
            HashMap<String,String> placeMap = null; //to store each place we fetch

            for(int i=0 ; i<count;i++) {
                //use getPlace method to fetch one place
                //then , add it to list of hashmap
                try {
                    placeMap = getPlace((JSONObject) jsonArray.get(i));
                    placesList.add(placeMap);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            return placesList;
        }

        //call this parse method whenever you create Data Parser
        //it will parse the JSON data n send it to getPlaces method
        //getPlaces method takes the JSONArray
        //will call getPlace method to fetch each element for each place and store it in a list
        //return the list to parse method

        List<HashMap<String,String>> parse(String jsonData)
        {
            JSONArray jsonArray = null;
            JSONObject jsonObject;

            try {
                jsonObject = new JSONObject(jsonData);
                jsonArray = jsonObject.getJSONArray("results");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (jsonArray != null) {
                return getPlaces(jsonArray);
            } else {
                return null;
            }
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
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HospitalActivity.this);
        View view;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view = LayoutInflater.from(HospitalActivity.this).inflate(R.layout.popup_loading, null);
        } else {
            view = LayoutInflater.from(HospitalActivity.this).inflate(R.layout.popup_loading_old, null);
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
//        Intent intent = new Intent(HospitalActivity.this, HomeActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}
