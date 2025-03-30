package com.example.snapshare.data.remote

import com.example.snapshare.data.local.PostDao
import com.example.snapshare.data.model.Post

class PostRemote(private val postDao: PostDao) {
    val allPosts = postDao.getAllPosts()

    suspend fun insertPost(post: Post) {
        postDao.insertPost(post)
    }

    suspend fun updatePost(post: Post) {
        postDao.updatePost(post)
    }

    suspend fun deletePost(post: Post) {
        postDao.deletePost(post)
    }
}