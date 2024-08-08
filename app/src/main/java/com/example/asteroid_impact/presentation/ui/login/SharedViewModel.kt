package com.example.asteroid_impact.presentation.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.asteroid_impact.presentation.repository.FirebaseAuthRepository
import kotlinx.coroutines.launch

class SharedViewModel(private val authRepository: FirebaseAuthRepository) : ViewModel() {

    private val _registerResult = MutableLiveData<Result<Unit>>()
    val registerResult: LiveData<Result<Unit>> = _registerResult

    fun registerUser(email: String, password: String) {
        viewModelScope.launch {
            val result = authRepository.registerWithEmail(email, password)
            _registerResult.value = result
        }
    }
}

class RegisterViewModelFactory(
    private val authRepository: FirebaseAuthRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            return SharedViewModel(authRepository) as T
        }
        throw IllegalArgumentException("뷰모델 클래스가 없음")
    }
}
