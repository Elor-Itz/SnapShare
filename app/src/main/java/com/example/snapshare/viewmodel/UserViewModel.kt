package com.example.snapshare.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.snapshare.data.local.AppDatabase
import com.example.snapshare.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userDao = AppDatabase.getDatabase(application).userDao()

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser

    init {
        // Automatically fetch the current user when the ViewModel is initialized
        fetchCurrentUser()
    }

    // Fetch the current user's data from Firestore and/or Room
    fun fetchCurrentUser() {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId != null) {
            viewModelScope.launch {
                try {
                    // First, check if the user exists in the local database
                    var user = withContext(Dispatchers.IO) {
                        userDao.getUserById(currentUserId)
                    }

                    if (user == null) {
                        // If not found locally, fetch from Firestore
                        user = fetchUserFromFirestore(currentUserId)
                        if (user != null) {
                            // Save the fetched user to the local database
                            saveUserToLocalDatabase(user)
                        }
                    }

                    _currentUser.postValue(user)
                } catch (e: Exception) {
                    Log.e("UserViewModel", "Error fetching current user: ${e.message}")
                    _currentUser.postValue(null)
                }
            }
        } else {
            Log.d("UserViewModel", "No user is currently logged in")
            _currentUser.postValue(null)
        }
    }

    // Fetch user data from Firestore
    private suspend fun fetchUserFromFirestore(userId: String): User? {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            if (document.exists()) {
                document.toObject(User::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("UserViewModel", "Error fetching user from Firestore: ${e.message}")
            null
        }
    }

    // Save the user to the local Room database
    private fun saveUserToLocalDatabase(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                userDao.insertUser(user)
                Log.d("UserViewModel", "User saved to local database: ${user.uid}")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error saving user to local database: ${e.message}")
            }
        }
    }
}