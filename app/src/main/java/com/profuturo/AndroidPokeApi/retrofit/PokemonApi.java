package com.profuturo.AndroidPokeApi.retrofit;

import android.content.Context;

import com.profuturo.AndroidPokeApi.NetworkUtil;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PokemonApi {
    private final static long CACHE_SIZE = 10 * 1024 * 1024;
    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(buildClient(context))
                    .baseUrl("https://pokeapi.co/api/v2/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getClientDetail(Context context) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(new OkHttpClient())
                    .baseUrl("https://pokeapi.co/api/v2/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static OkHttpClient buildClient(Context context) {
        final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = chain -> {
            Response originalResponse = chain.proceed(chain.request());
            if (NetworkUtil.hasNetwork(context)) {
                int maxAge = 60;
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                int maxStale = 60 * 60 * 24 * 28;
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        };
        Cache cache = new Cache(context.getCacheDir(), CACHE_SIZE);
        return new OkHttpClient
                .Builder()
                .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .cache(cache)
                .build();
    }
}