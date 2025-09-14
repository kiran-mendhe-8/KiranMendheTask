package com.mk.kiranmendhetask.di

import android.content.Context
import com.mk.kiranmendhetask.data.local.HoldingsDatabase
import com.mk.kiranmendhetask.data.local.HoldingsDao
import com.mk.kiranmendhetask.data.local.LocalMapper
import com.mk.kiranmendhetask.data.remote.HoldingsApiService
import com.mk.kiranmendhetask.data.remote.HoldingsMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://35dee773a9ec441e9f38d5fc249406ce.api.mockbin.io/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideHoldingsApiService(retrofit: Retrofit): HoldingsApiService {
        return retrofit.create(HoldingsApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideHoldingsDatabase(@ApplicationContext context: Context): HoldingsDatabase {
        return HoldingsDatabase.getDatabase(context)
    }
    
    @Provides
    @Singleton
    fun provideHoldingsDao(database: HoldingsDatabase): HoldingsDao {
        return database.holdingsDao()
    }
    
    @Provides
    @Singleton
    fun provideHoldingsMapper(): HoldingsMapper {
        return HoldingsMapper()
    }
    
    @Provides
    @Singleton
    fun provideLocalMapper(): LocalMapper {
        return LocalMapper()
    }
}
