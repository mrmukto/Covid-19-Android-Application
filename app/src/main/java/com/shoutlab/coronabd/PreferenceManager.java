package com.shoutlab.coronabd;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class PreferenceManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private static final String PREF_NAME = "COVID19";
    private static final String DEFAULT_STR_RESPONSE = "Data Not Available";
    private static final boolean DEFAULT_BOOL_RESPONSE = false;

    private static final String LANGUAGE = "LANGUAGE";
    private static final String IS_LOGGED_IN = "IS_LOGGED_IN";
    private static final String IS_PROFILE_COMPLETE = "IS_PROFILE_COMPLETE";

    private static final String UUID = "UUID";
    private static final String NAME = "NAME";
    private static final String COUNTRY = "COUNTRY";

    @SuppressLint("CommitPrefEdits")
    public PreferenceManager(Context context) {
        this.pref = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        this.editor = pref.edit();
    }

    /*============= Setters =============*/
    void setLanguage(String language){
        editor.putString(LANGUAGE, language);
        editor.apply();
    }
    void setStatus(String b){
        editor.putString("status",b);
        editor.apply();
    }

    public String getStatus(){
        return pref.getString("status","No");
    }

    void setIsLoggedIn(){
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.apply();
    }

    void setIsProfileComplete(){
        editor.putBoolean(IS_PROFILE_COMPLETE, true);
        editor.apply();
    }

    void setUUID(String mobile){
        editor.putString(UUID, mobile);
        editor.apply();
    }

    void setCountry(String country){
        editor.putString(COUNTRY, country);
        editor.apply();
    }

    void setName(String name){
        editor.putString(NAME, name);
        editor.apply();
    }

    public void setRegisterTime(){
        editor.putString("time",String.valueOf(System.currentTimeMillis()));
        editor.apply();
    }
    public String getRegisterTime(){
        return pref.getString("time","0");
    }


    /*============= Getters =============*/
    String getLanguage(){
        return pref.getString(LANGUAGE, DEFAULT_STR_RESPONSE);
    }

    public boolean getIsLoggedIn(){
        return pref.getBoolean(IS_LOGGED_IN, DEFAULT_BOOL_RESPONSE);
    }

    boolean getIsProfileComplete(){
        return pref.getBoolean(IS_PROFILE_COMPLETE, DEFAULT_BOOL_RESPONSE);
    }

    public String getUUID(){
        return pref.getString(UUID, DEFAULT_STR_RESPONSE);
    }

    String getCountry(){
        return pref.getString(COUNTRY, DEFAULT_STR_RESPONSE);
    }

    public String getName(){
        return pref.getString(NAME, DEFAULT_STR_RESPONSE);
    }



    public void removeAll(){
        pref.edit().clear().apply();
    }
}
