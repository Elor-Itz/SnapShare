package com.example.snapshare.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val uid: String,
    val firstName: String,
    val lastName: String,
    val email: String    
)