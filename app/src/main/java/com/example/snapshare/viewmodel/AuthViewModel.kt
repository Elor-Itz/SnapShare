package com.example.snapshare.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.snapshare.repository.UserRepository
import com.google.firebase.auth.FirebaseUser

class AuthViewModel : ViewModel() {

    private val authRepository = UserRepository()

    private val _authState = MutableLiveData<FirebaseUser?>()
    val authState: LiveData<FirebaseUser?> get() = _authState

    init {
        _authState.value = authRepository.getCurrentUser()
    }

    fun registerUser(email: String, password: String) {
        authRepository.registerUser(email, password) { user ->
            _authState.value = user
        }
    }

    fun loginUser(email: String, password: String) {
        authRepository.loginUser(email, password) { user ->
            _authState.value = user
        }
    }

    fun logoutUser() {
        authRepository.logoutUser()
        _authState.value = null
    }
}