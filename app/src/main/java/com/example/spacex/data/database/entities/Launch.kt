package com.example.spacex.data.database.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.spacex.models.network.launches.*
import com.example.spacex.utils.converters.LinksConverter
import kotlinx.parcelize.Parcelize
import java.util.*

@Entity
@Parcelize
@TypeConverters(LinksConverter::class)
data class Launch(
    @PrimaryKey
    val launchId: String,
    val details: String?,
    val flight_number: Int?,
    val date_unix: Long,
    val name: String?,
    val links: Links?,
    val ships: List<String>?,
    val success: Boolean?,
) : Parcelable



