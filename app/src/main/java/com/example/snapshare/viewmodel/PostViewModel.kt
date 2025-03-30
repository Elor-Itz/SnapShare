package com.example.snapshare.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.snapshare.data.local.AppDatabase
import com.example.snapshare.data.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val postDao = AppDatabase.getDatabase(application).postDao()
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

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

    // Fetch posts from Firestore and cache them in the local database
    fun fetchPostsFromFirestore() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Fetch posts from Firestore
                val snapshot = firestore.collection("posts").get().await()
                val fetchedPosts = snapshot.toObjects(Post::class.java)

                // Cache posts in the local database
                postDao.insertPosts(fetchedPosts)

                // Load posts from the local database
                val cachedPosts = postDao.getAllPosts()
                _allPosts.postValue(cachedPosts)
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error fetching posts from Firestore: ${e.message}")

                // Fallback to loading posts from the local database
                val cachedPosts = postDao.getAllPosts()
                _allPosts.postValue(cachedPosts)
            }
        }
    }

    // Fetch a single post by ID
    fun fetchPostById(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val post = postDao.getPostById(id)
                _currentPost.postValue(post)
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error fetching post by ID: ${e.message}")
            }
        }
    }

    // Insert a new post into Firestore and the local database
    fun insertPost(post: Post): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Save to Firestore
                val document = firestore.collection("posts").document()
                val postWithId = post.copy(id = document.id)
                document.set(postWithId).await()

                // Save to local database
                postDao.insertPost(postWithId)
                fetchAllPosts() // Refresh the list of posts
                result.postValue(true)
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error inserting post: ${e.message}")
                result.postValue(false)
            }
        }
        return result
    }

    // Update an existing post in Firestore and the local database
    fun updatePost(post: Post): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Update Firestore
                firestore.collection("posts").document(post.id).set(post).await()

                // Update local database
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

    // Delete a post from Firestore and the local database
    fun deletePost(post: Post): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Delete from Firestore
                firestore.collection("posts").document(post.id).delete().await()

                // Delete from local database
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