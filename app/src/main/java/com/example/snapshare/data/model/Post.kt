package com.example.snapshare.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey val postId: String,
    val userId: String, // user's ID who created the post
    val title: String,
    val content: String,
    val imageUrl: String? = null,
    val timestamp: Long
)