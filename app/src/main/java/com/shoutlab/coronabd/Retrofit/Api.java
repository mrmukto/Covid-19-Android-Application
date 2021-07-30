package com.shoutlab.coronabd.Retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Api {

    @FormUrlEncoded
    @POST("createProfile")
    Call<ResponseBody> createProfile(
            @Field("name") String name,
            @Field("country") String country,
            @Field("deviceToken") String device_token
    );

    @FormUrlEncoded
    @POST("homeData")
    Call<ResponseBody> homeData(
            @Field("uuid") String uuid,
            @Field("country") String country
    );

    @FormUrlEncoded
    @POST("donationList")
    Call<ResponseBody> donationList(
            @Field("uuid") String uuid
    );

    @FormUrlEncoded
    @POST("getSituationReport")
    Call<ResponseBody> getSituationReport(
            @Field("uuid") String uuid
    );

    @FormUrlEncoded
    @POST("helplineList")
    Call<ResponseBody> helplineList(
            @Field("uuid") String uuid
    );

    @FormUrlEncoded
    @POST("faqList")
    Call<ResponseBody> faqList(
            @Field("uuid") String uuid
    );

    @FormUrlEncoded
    @POST("covidData")
    Call<ResponseBody> covidData(
            @Field("uuid") String uuid
    );

    @FormUrlEncoded
    @POST("volunteerList")
    Call<ResponseBody> getVolunteerList(
            @Field("uuid") String uuid
    );

    @FormUrlEncoded
    @POST("maskData")
    Call<ResponseBody> maskData(
            @Field("uuid") String uuid
    );

    @FormUrlEncoded
    @POST("getMyth")
    Call<ResponseBody> getMyth(
            @Field("uuid") String uuid
    );

    @FormUrlEncoded
    @POST("getHygiene")
    Call<ResponseBody> getHygiene(
            @Field("uuid") String uuid
    );

    @FormUrlEncoded
    @POST("handWashData")
    Call<ResponseBody> handWashData(
            @Field("uuid") String uuid
    );

    @FormUrlEncoded
    @POST("mentalHealth")
    Call<ResponseBody> mentalHealth(
            @Field("uuid") String uuid
    );

    @FormUrlEncoded
    @POST("coronaHospitals")
    Call<ResponseBody> coronaHospitals(
            @Field("uuid") String uuid
    );

    @FormUrlEncoded
    @POST("getTestQuestion")
    Call<ResponseBody> getTestQuestion(
            @Field("uuid") String uuid
    );

    @FormUrlEncoded
    @POST("checkInfection")
    Call<ResponseBody> checkInfection(
            @Field("uuid") String uuid,
            @Field("age") String age,
            @Field("answer") String answer
    );
}
