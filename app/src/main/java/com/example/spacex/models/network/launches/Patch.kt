package com.example.spacex.models.network.launches

import android.os.Parcelable
import androidx.room.TypeConverters
import com.example.spacex.utils.converters.PatchConverter
import kotlinx.parcelize.Parcelize

@TypeConverters(PatchConverter::class)
@Parcelize
data class Patch(
    val large: String?,
    val small: String?
) : Parcelable