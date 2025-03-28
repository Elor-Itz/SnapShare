package com.example.snapshare.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
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
        fetchCurrentUser()
    }

    // Load current user
    fun fetchCurrentUser() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val firstName = document.getString("firstName") ?: "No Name"
                    val lastName = document.getString("lastName") ?: ""
                    val profilePictureUrl = document.getString("profilePictureUrl") ?: ""
                    val email = auth.currentUser?.email ?: "No Email"

                    val user = User(
                        uid = userId,
                        firstName = firstName,
                        lastName = lastName,
                        profilePictureUrl = profilePictureUrl,
                        email = email
                    )

                    _currentUser.postValue(user) // Update LiveData
                }
            }
            .addOnFailureListener {
                _currentUser.postValue(null) // Handle failure case
            }
    }

    // Fetch user from Firestore
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

    // Save user to local database
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

    // Update user profile
    fun updateUserProfile(firstName: String, lastName: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        val currentUserId = auth.currentUser?.uid

        if (currentUserId == null) {
            result.value = false
            return result
        }

        viewModelScope.launch {
            try {
                val updates = mapOf(
                    "firstName" to firstName,
                    "lastName" to lastName
                )

                // Update Firestore
                firestore.collection("users").document(currentUserId).update(updates).await()

                // Update Local Database
                withContext(Dispatchers.IO) {
                    val updatedUser = _currentUser.value?.copy(firstName = firstName, lastName = lastName)
                    if (updatedUser != null) {
                        userDao.insertUser(updatedUser)
                        _currentUser.postValue(updatedUser)
                    }
                }

                result.postValue(true)
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error updating user profile: ${e.message}")
                result.postValue(false)
            }
        }

        return result
    }
}