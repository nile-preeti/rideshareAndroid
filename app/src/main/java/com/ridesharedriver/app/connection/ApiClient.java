package com.ridesharedriver.app.connection;

import static com.ridesharedriver.app.Server.Server.BASE_URL;

import com.ridesharedriver.app.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Saurabh on 13/03/2020.
 */

//this is Api Client
public class ApiClient {
    private static Retrofit retrofit;
//    private static final String BASE_URL = "https://app.ridesharerates.com/";//public ip

    //creating retrofit instance
    private static Retrofit getClient() {

        OkHttpClient client;
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(interceptor)
                    .readTimeout(60, TimeUnit.SECONDS).build();
        } else {
            client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS).build();
        }
        if (retrofit == null) {
//            Gson gson = new GsonBuilder()
//                    .setLenient()
//                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }

        return retrofit;
    }

    public static ApiNetworkCall getApiService() {
        return getClient().create(ApiNetworkCall.class);
    }
}