package com.mikeschvedov.spacex.di

import com.mikeschvedov.spacex.data.database.repository.DataBaseRepository
import com.mikeschvedov.spacex.data.database.repository.DataBaseRepositoryImpl
import com.mikeschvedov.spacex.data.mediator.ContentMediator
import com.mikeschvedov.spacex.data.mediator.ContentMediatorImpl
import com.mikeschvedov.spacex.data.network.ApiManager
import com.mikeschvedov.spacex.data.network.ApiManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BindingModule {

    @Binds
    @Singleton
    abstract fun bindRepository(repository: DataBaseRepositoryImpl): DataBaseRepository

    @Binds
    @Singleton
    abstract fun bindRemoteApi(apiManager: ApiManagerImpl): ApiManager

    @Binds
    @Singleton
    abstract fun bindContentMediator(contentMediator: ContentMediatorImpl): ContentMediator
}
