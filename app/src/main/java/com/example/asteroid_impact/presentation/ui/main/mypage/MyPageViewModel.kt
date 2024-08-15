package com.example.asteroid_impact.presentation.ui.main.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.asteroid_impact.presentation.repository.FirebaseAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(val authRepository: FirebaseAuthRepository): ViewModel() {

    private val _accountDeleteResult = MutableLiveData<Result<Unit>?>()
    val accountDeleteResult: LiveData<Result<Unit>?> get() = _accountDeleteResult

    fun signOut() {
        authRepository.signOut()
    }

    fun deleteAccount(email: String, password: String) {
        viewModelScope.launch {
            // 먼저 재인증
            val reAuthResult = authRepository.reAuthenticateUser(email, password)
            if (reAuthResult.isSuccess) {
                // 재인증 성공 시 계정 삭제
                val user = authRepository.getCurrentUser()
                if (user != null) {
                    val deleteResult = authRepository.deleteAccount(user)
                    _accountDeleteResult.value = deleteResult
                } else {
                    _accountDeleteResult.value = Result.failure(IllegalStateException("유저 정보가 없음"))
                }
            } else {
                _accountDeleteResult.value = reAuthResult
            }
        }
    }
}

class MyPageViewModelFactory(
    private val authRepository: FirebaseAuthRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPageViewModel::class.java)) {
            return MyPageViewModel(authRepository) as T
        }
        throw IllegalArgumentException("뷰모델 클래스가 없음")
    }
}