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

    override suspend fun sendEmailVerification(): Result<Unit> {
        val user = firebaseAuth.currentUser
        return if (user != null) {
            try {
                user.sendEmailVerification().await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.failure(IllegalStateException("유저가 로그인 되지 않은 상태"))
        }
    }
}