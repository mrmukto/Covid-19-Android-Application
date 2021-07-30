package com.shoutlab.coronabd.Retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    //https://covid19.rafat.me/api/
    //http://103.198.136.1/COVID19/public/api/

    public static final String BASE = "http://103.198.136.1/COVID19/public/";
    private static final String BASE_URL = "https://covid19.rafat.me/api/";


    private static final String currenturl="https://api.covid19api.com/live/country/bangladesh/status/confirmed";

    private static RetrofitClient mInstance;
    private Retrofit retrofit;

    private RetrofitClient(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance(){
        if(mInstance == null){
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }

    public Api getApi(){
        return retrofit.create(Api.class);
    }

}
