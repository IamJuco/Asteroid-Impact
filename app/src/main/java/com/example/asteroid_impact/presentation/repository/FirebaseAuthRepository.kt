package com.example.asteroid_impact.presentation.repository

import com.google.firebase.auth.FirebaseUser

interface FirebaseAuthRepository {
    suspend fun registerWithEmail(email: String, password: String): Result<Unit>
    suspend fun sendEmailVerification(): Result<Unit>
    fun getCurrentUser(): FirebaseUser?
    fun signOut()
}