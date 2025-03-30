package com.example.snapshare.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val content: String,
    val imageUrl: String? = null,
    val timestamp: Long
)