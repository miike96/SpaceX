package com.mikeschvedov.spacex.models.network.launches

import android.os.Parcelable
import androidx.room.TypeConverters
import com.mikeschvedov.spacex.utils.converters.LinksConverter
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@Parcelize
@TypeConverters(LinksConverter::class)
data class Links(
    //val article: String,
    val patch: Patch,
   // val presskit: String,
    //val webcast: String,
    //val wikipedia: String,
    //val youtube_id: String
) : Parcelable