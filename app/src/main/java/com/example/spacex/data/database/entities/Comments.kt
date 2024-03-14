package com.example.spacex.data.database.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.TypeConverters
import com.example.spacex.utils.converters.LinksConverter
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
@TypeConverters(LinksConverter::class)
data class Comments(
    val comment: String,
    val launchId: String
) : Parcelable