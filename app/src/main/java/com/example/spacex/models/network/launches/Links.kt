package com.example.spacex.models.network.launches

import android.os.Parcelable
import androidx.room.TypeConverters
import com.example.spacex.utils.converters.LinksConverter
import kotlinx.parcelize.Parcelize


@Parcelize
@TypeConverters(LinksConverter::class)
data class Links(
    val patch: Patch,
) : Parcelable