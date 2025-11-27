package com.example.myapplication2.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.myapplication2.ApiService;

public class ApiClient {
    private static final String BASE_URL = "https://iceflowers.swuitapp.com/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // ✅ Add this method
    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
}
