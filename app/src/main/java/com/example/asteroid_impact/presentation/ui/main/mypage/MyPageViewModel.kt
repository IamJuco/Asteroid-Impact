package com.example.asteroid_impact.presentation.ui.main.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.asteroid_impact.presentation.repository.FirebaseAuthRepository

class MyPageViewModel(private val authRepository: FirebaseAuthRepository): ViewModel() {

    fun signOut() {
        authRepository.signOut()
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