package com.example.asteroid_impact.presentation.repository

import com.google.firebase.auth.FirebaseUser

interface FirebaseAuthRepository {
    suspend fun registerWithEmail(email: String, password: String): Result<Unit>
    suspend fun sendEmailVerification(): Result<Unit>
    suspend fun deleteAccount(user: FirebaseUser): Result<Unit>
    suspend fun reAuthenticateUser(email: String, password: String): Result<Unit>
    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser>
    suspend fun changePassword(email: String): Result<Unit>
    fun getCurrentUser(): FirebaseUser?
    fun signOut()
}