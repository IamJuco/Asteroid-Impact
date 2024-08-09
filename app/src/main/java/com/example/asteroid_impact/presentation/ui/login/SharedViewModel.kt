package com.example.asteroid_impact.presentation.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.asteroid_impact.presentation.repository.FirebaseAuthRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SharedViewModel(private val authRepository: FirebaseAuthRepository) : ViewModel() {

    private val _registerResult = MutableLiveData<Result<Unit>>()
    val registerResult: LiveData<Result<Unit>> = _registerResult

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private var timerJob: Job? = null

    fun registerUser(email: String, password: String) {
        viewModelScope.launch {
            val result = authRepository.registerWithEmail(email, password)
            _registerResult.value = result

            if (result.isSuccess) {
                emailVerificationTimer()
            }
        }
    }

    fun saveEmailForVerification(email: String) {
        _email.value = email
    }

    fun sendEmailVerification() {
        viewModelScope.launch {
            val result = authRepository.sendEmailVerification()
            if (result.isFailure) {
                //TODO 인증 실패시 처리 할것 ( 텍스트나 UI 추가할것 )
            } else {
                authRepository.sendEmailVerification()
            }
        }
    }

    fun checkEmailVerification(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user != null) {
                try {
                    user.reload().await()
                } catch (e: Exception) {
                    onResult(false)
                    return@launch
                }
            }

            val isVerified = user?.isEmailVerified ?: false
            onResult(isVerified)

            if (isVerified) {
                timerJob?.cancel()
            }
        }
    }

    fun deleteAccountAndReAuthentication(email: String, password: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val reAuthResult = authRepository.reAuthenticateUser(email, password)
            if (reAuthResult.isSuccess) {
                val user = authRepository.getCurrentUser()
                if (user != null) {
                    val deleteResult = authRepository.deleteAccount(user)
                    onResult(deleteResult)
                } else {
                    onResult(Result.failure(IllegalStateException("유저가 존재하지 않음")))
                }
            } else {
                onResult(reAuthResult)
            }
        }
    }

    private fun emailVerificationTimer() {
        timerJob?.cancel()

        timerJob = viewModelScope.launch {
            delay(5 * 60 * 1000)
            val user = authRepository.getCurrentUser()
            if (user != null && !user.isEmailVerified) {
                authRepository.deleteAccount(user)
            }
        }
    }
}

class SharedViewModelFactory(
    private val authRepository: FirebaseAuthRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            return SharedViewModel(authRepository) as T
        }
        throw IllegalArgumentException("뷰모델 클래스가 없음")
    }
}