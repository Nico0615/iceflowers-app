package com.example.myapplication2;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // ==========================
    // Base URL for API
    // ==========================
    private static final String BASE_URL = "https://iceflowers.swuitapp.com/";

    private static Retrofit retrofit = null;

    // Get Retrofit instance
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // Get API service
    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
}
