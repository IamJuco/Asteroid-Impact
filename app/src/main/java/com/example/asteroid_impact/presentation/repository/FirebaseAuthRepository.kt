package com.example.asteroid_impact.presentation.repository

interface FirebaseAuthRepository {
    suspend fun registerWithEmail(email: String, password: String): Result<Unit>
}