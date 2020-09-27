package com.awshakam1998.sitterapp.Constant;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface Api {
    @GET
    Call<String>getstringonline(@Url String url);
}
