package com.example.snapshare.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.snapshare.data.local.AppDatabase
import com.example.snapshare.data.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val postDao = AppDatabase.getDatabase(application).postDao()

    private val _allPosts = MutableLiveData<List<Post>>()
    val allPosts: LiveData<List<Post>> get() = _allPosts

    private val _currentPost = MutableLiveData<Post?>()
    val currentPost: LiveData<Post?> get() = _currentPost

    init {
        fetchAllPosts()
    }

    // Fetch all posts from the local database
    fun fetchAllPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val posts = postDao.getAllPosts()
                _allPosts.postValue(posts)
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error fetching posts: ${e.message}")
            }
        }
    }

    // Fetch a single post by ID
    fun fetchPostById(postId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val post = postDao.getPostById(postId)
                _currentPost.postValue(post)
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error fetching post by ID: ${e.message}")
            }
        }
    }

    // Insert a new post into the local database
    fun insertPost(post: Post): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                postDao.insertPost(post)
                fetchAllPosts() // Refresh the list of posts
                result.postValue(true)
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error inserting post: ${e.message}")
                result.postValue(false)
            }
        }
        return result
    }

    // Update an existing post
    fun updatePost(post: Post): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                postDao.updatePost(post)
                fetchAllPosts() // Refresh the list of posts
                result.postValue(true)
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error updating post: ${e.message}")
                result.postValue(false)
            }
        }
        return result
    }

    // Delete a post
    fun deletePost(post: Post): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                postDao.deletePost(post)
                fetchAllPosts() // Refresh the list of posts
                result.postValue(true)
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error deleting post: ${e.message}")
                result.postValue(false)
            }
        }
        return result
    }
}