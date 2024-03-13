package com.mikeschvedov.spacex.utils.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.mikeschvedov.spacex.models.network.launches.Links
import com.mikeschvedov.spacex.models.network.launches.Patch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LinksConverter {
    @TypeConverter
    fun fromLinks(value: Links): String {
        val gson = Gson()
        return gson.toJson(value)
    }

    @TypeConverter
    fun toLinks(value: String): Links {
        val gson = Gson()
        return gson.fromJson(value, Links::class.java)
    }
}