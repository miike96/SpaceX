package com.mikeschvedov.spacex.models.network.launches

import android.os.Parcelable
import androidx.room.TypeConverters
import com.mikeschvedov.spacex.utils.converters.PatchConverter
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@TypeConverters(PatchConverter::class)
@Parcelize
data class Patch(
    val large: String?,
    val small: String?
) : Parcelable