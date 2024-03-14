package com.example.spacex.data.database.daos

import androidx.room.Dao

@Dao
interface CommentsDao {
    suspend fun addComment()
    suspend fun deleteComment()
}