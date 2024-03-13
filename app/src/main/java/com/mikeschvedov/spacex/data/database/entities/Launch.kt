package com.mikeschvedov.spacex.data.database.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.mikeschvedov.spacex.models.network.launches.*
import com.mikeschvedov.spacex.utils.converters.LinksConverter
import com.mikeschvedov.spacex.utils.converters.PatchConverter
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



