package com.example.asteroid_impact.data.repository

import android.util.Log
import com.example.asteroid_impact.presentation.repository.FirebaseAuthRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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

    override suspend fun deleteAccount(user: FirebaseUser): Result<Unit> {
        return try {
            user.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun reAuthenticateUser(email: String, password: String): Result<Unit> {
        val user = firebaseAuth.currentUser
        return if (user != null) {
            val credential = EmailAuthProvider.getCredential(email, password)
            try {
                user.reauthenticate(credential).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.failure(IllegalStateException("유저가 로그인 되지 않은 상태"))
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val auth = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(auth.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun changePassword(email: String): Result<Unit> {
        return try {
            Firebase.auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}