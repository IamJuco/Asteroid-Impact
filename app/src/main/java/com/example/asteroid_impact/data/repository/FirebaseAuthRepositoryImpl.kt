package com.example.asteroid_impact.data.repository

import com.example.asteroid_impact.presentation.repository.FirebaseAuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepositoryImpl : FirebaseAuthRepository {
    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override suspend fun registerWithEmail(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}