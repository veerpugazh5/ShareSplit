package com.sharesplit.app.di

import com.sharesplit.app.data.repository.*
import com.sharesplit.app.data.repository.impl.FirebaseAuthRepository
import com.sharesplit.app.data.repository.impl.FirebaseGroupRepository
import com.sharesplit.app.data.repository.impl.GoogleDriveRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        firebaseAuthRepository: FirebaseAuthRepository
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindGroupRepository(
        firebaseGroupRepository: FirebaseGroupRepository
    ): GroupRepository

    @Binds
    @Singleton
    abstract fun bindGoogleDriveRepository(
        googleDriveRepositoryImpl: GoogleDriveRepositoryImpl
    ): GoogleDriveRepository
} 