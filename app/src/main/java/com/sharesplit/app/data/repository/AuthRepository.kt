package com.sharesplit.app.data.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.sharesplit.app.data.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUser(): Flow<User?>
    suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<User>
    suspend fun signOut()
    suspend fun updateUserProfile(user: User): Result<User>
    suspend fun updateFCMToken(token: String): Result<Unit>
} 