package com.example.spacex.models.network.launches

import android.os.Parcelable
import androidx.room.TypeConverters
import com.example.spacex.utils.converters.LinksConverter
import kotlinx.parcelize.Parcelize


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