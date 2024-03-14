package com.example.spacex.di

import android.content.Context
import android.net.ConnectivityManager
import com.example.spacex.data.network.SpaceXApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val SPACEX_API_BASE_URL = "https://api.spacexdata.com/"

    //  ------------ Retrofit ------------ //
    @Provides
    fun provideRetrofit(
        httpClient: OkHttpClient,
        gsonConverterFactory: Converter.Factory
    ): Retrofit = Retrofit.Builder()
        .client(httpClient)
        .baseUrl(SPACEX_API_BASE_URL)
        .addConverterFactory(gsonConverterFactory)
        .build()

    //  ------------ OkHttp ------------ //
    @Provides
    fun provideOKHTTPClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient =
        OkHttpClient().newBuilder()
            //.addInterceptor(loggingInterceptor)
           .build()

    //  ------------ SpaceXApi ------------ //
    @Provides
    fun provideSpaceXApi(retrofit: Retrofit): SpaceXApi = retrofit.create(SpaceXApi::class.java)

    //  ------------ GsonFactory ------------ //
    @Provides
    fun provideGsonFactory(): Converter.Factory = GsonConverterFactory.create()

    //  ------------ LoggingInterceptor ------------ //
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY //full log
    }

    //  ------------ ConnectivityManager ------------ //
    @Provides
    fun provideConnectivityManager(@ApplicationContext appContext: Context): ConnectivityManager {
        return  appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

}