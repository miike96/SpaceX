package com.example.spacex.utils.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.example.spacex.models.network.launches.Links

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