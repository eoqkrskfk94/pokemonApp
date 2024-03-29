package com.mj.data.di

import android.content.Context
import com.google.gson.GsonBuilder
import com.mj.data.api.ApiClient
import com.mj.data.api.PokemonDetailApiService
import com.mj.data.api.PokemonSearchApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Singleton
    @Provides
    fun providePokemonSearchApiService(
        @PokemonSearchRetrofit retrofit: Retrofit
    ): PokemonSearchApiService =
        retrofit.create(PokemonSearchApiService::class.java)

    @Singleton
    @Provides
    fun providePokemonDetailApiService(
        @PokemonDetailRetrofit retrofit: Retrofit
    ): PokemonDetailApiService =
        retrofit.create(PokemonDetailApiService::class.java)


    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class PokemonSearchRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class PokemonDetailRetrofit


    @Singleton
    @Provides
    @PokemonSearchRetrofit
    fun providePokemonSearchRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiClient.POKEMON_SEARCH_BASE_URL)
            .addConverterFactory(gsonConverterFactory)
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    @PokemonDetailRetrofit
    fun providePokemonDetailRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiClient.POKEMON_API_BASE_URL)
            .addConverterFactory(gsonConverterFactory)
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun provideGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create(
            GsonBuilder().create()
        )
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        networkCacheInterceptor: Interceptor,
        @ApplicationContext appContext: Context
    ): OkHttpClient {
        val cacheSize = 10 * 1024 * 1024 // 10 MB
        val httpCacheDirectory = File(appContext.cacheDir, "http-cache")
        val cache = Cache(httpCacheDirectory, cacheSize.toLong())

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BASIC

        return OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .addNetworkInterceptor(networkCacheInterceptor)
            .build()
    }


    @Singleton
    @Provides
    fun provideNetworkCacheInterceptor(): Interceptor {
        return Interceptor {
            val response = it.proceed(it.request())

            val cacheControl = CacheControl.Builder()
                .maxAge(5, TimeUnit.MINUTES)
                .build()

            response.newBuilder()
                .header("Cache-Control", cacheControl.toString())
                .build()
        }
    }



}