package com.mk.kiranmendhetask.di

import com.mk.kiranmendhetask.data.local.HoldingsDao
import com.mk.kiranmendhetask.data.local.LocalMapper
import com.mk.kiranmendhetask.data.remote.HoldingsApiService
import com.mk.kiranmendhetask.data.remote.HoldingsMapper
import com.mk.kiranmendhetask.data.repository.HoldingsRepositoryImpl
import com.mk.kiranmendhetask.domain.repository.HoldingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideHoldingsRepository(
        apiService: HoldingsApiService,
        dao: HoldingsDao,
        remoteMapper: HoldingsMapper,
        localMapper: LocalMapper
    ): HoldingsRepository {
        return HoldingsRepositoryImpl(apiService, dao, remoteMapper, localMapper)
    }
}
