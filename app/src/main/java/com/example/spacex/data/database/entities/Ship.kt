package com.example.spacex.data.database.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Ship(
    @PrimaryKey
    val shipId: String,
    val image: String?,
    val launches: List<String>?,
    val legacy_id: String?,
    val model: String?,
    val name: String?,
    val status: String?,
    val type: String?,
    val year_built: Int?
) : Parcelable

