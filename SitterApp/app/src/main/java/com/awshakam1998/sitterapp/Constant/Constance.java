package com.awshakam1998.sitterapp.Constant;

import com.awshakam1998.sitterapp.Online.RetrofitClient;

import retrofit2.Retrofit;

public class Constance {
    public static final String baseurl="https://maps.googleapis.com";
    public static Api getapi(){
        return RetrofitClient.getRetrofit(baseurl).create(Api.class);
    };
}
