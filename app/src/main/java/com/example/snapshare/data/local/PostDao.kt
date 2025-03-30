package com.example.snapshare.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.snapshare.data.model.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE uid = :userId")
    fun getUserById(userId: String): User?

    @Query("SELECT * FROM users")
    fun getAllUsers(): List<User>
}