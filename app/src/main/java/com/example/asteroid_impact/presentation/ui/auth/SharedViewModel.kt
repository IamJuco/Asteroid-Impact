package com.example.asteroid_impact.presentation.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.asteroid_impact.Constants
import com.example.asteroid_impact.presentation.repository.FirebaseAuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SharedViewModel(private val authRepository: FirebaseAuthRepository) : ViewModel() {

    private val _registerResult = MutableLiveData<Result<Unit>>()
    val registerResult: LiveData<Result<Unit>?> = _registerResult

    private val _registerResultForEmailVerification = MutableLiveData<Result<Unit>?>()
    val registerResultForEmailVerification: LiveData<Result<Unit>?> =
        _registerResultForEmailVerification

    private val _email = MutableLiveData<String>()
    val email: MutableLiveData<String> get() = _email

    private val _loginResult = MutableLiveData<Result<FirebaseUser>?>()
    val loginResult: LiveData<Result<FirebaseUser>?> get() = _loginResult

    private val _sendVerifyCodeForChangePassword = MutableLiveData<Result<Unit>?>()
    val sendVerifyCodeForChangePassword: LiveData<Result<Unit>?> = _sendVerifyCodeForChangePassword

    private var timerJob: Job? = null

    fun loginUser(email: String, password: String) {
        viewModelScope.launch{
            val result = authRepository.signInWithEmail(email, password)
            _loginResult.value = result
        }
    }

    fun registerUser(password: String) {
        val email = _email.value
        if (email != null) {
            viewModelScope.launch {
                val result = authRepository.registerWithEmail(email, password)
                _registerResult.value = result
                if (result.isFailure) {
                    Log.d("0526registerUser", "회원가입 실패 ${result.exceptionOrNull()?.message}")
                }
            }
        }
    }

    fun registerUserForEmailVerification(email: String, password: String) {
        _email.value = email
        viewModelScope.launch {
            val result = authRepository.registerWithEmail(email, password)
            _registerResultForEmailVerification.value = result

            if (result.isSuccess) {
                emailVerificationTimer(email, password)
            }
        }
    }

    fun sendEmailVerification() {
        viewModelScope.launch {
            val result = authRepository.sendEmailVerification()
            if (result.isFailure) {
                Log.d(
                    "0526sendEmailVerification",
                    "인증 코드 보내기 실패 ${result.exceptionOrNull()?.message}"
                )
            } else {
                authRepository.sendEmailVerification()
            }
        }
    }

    fun clearRegisterResult() {
        _registerResultForEmailVerification.value = null
    }

    fun clearLoginResult() {
        _loginResult.value = null
    }

    fun clearSendVerifyCodeForChangePassword() {
        _sendVerifyCodeForChangePassword.value = null
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

    fun deleteAccountAndReAuthentication(
        email: String,
        password: String,
        onResult: (Result<Unit>) -> Unit
    ) {
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

    private fun emailVerificationTimer(email: String, password: String) {
        timerJob?.cancel()

        timerJob = viewModelScope.launch {
            delay(1 * 60 * 1000)  // 1분 대기

            val user = authRepository.getCurrentUser()
            if (user != null && !user.isEmailVerified) {
                val reAuthResult = authRepository.reAuthenticateUser(email, password)
                if (reAuthResult.isSuccess) {
                    val deleteResult = authRepository.deleteAccount(user)
                    if (deleteResult.isFailure) {
                        Log.d(
                            "0526emailVerificationTimer",
                            " 계정 삭제 실패 ${deleteResult.exceptionOrNull()?.message}"
                        )
                    }
                } else {
                    // 재인증 실패
                }
            }
        }
    }

    //TODO 앱 강제종료시 미리 회원가입된 정보를 삭제시켜야함
    fun accountDelete() {
        val email = _email.value
        val password = Constants.USER_TEMP_PASSWORD
        if (email != null) {
            viewModelScope.launch {
                val reAuthResult = authRepository.reAuthenticateUser(email, password)
                if (reAuthResult.isSuccess) {
                    val user = authRepository.getCurrentUser()
                    if (user != null) {
                        val deleteResult = authRepository.deleteAccount(user)
                        if (deleteResult.isFailure) {
                            Log.e(
                                "0526accountDeleteForOnCleared",
                                "계정 삭제 실패: ${deleteResult.exceptionOrNull()?.message}"
                            )
                        }
                    }
                } else {
                    Log.e(
                        "0526accountDeleteForOnCleared",
                        "재인증 실패: ${reAuthResult.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    fun sendVerifyCodeForChangePassword(email: String) {
        viewModelScope.launch {
            val result = authRepository.changePassword(email)
            _sendVerifyCodeForChangePassword.value = result
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