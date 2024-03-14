package com.example.spacex.utils.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.example.spacex.models.network.launches.Patch

class PatchConverter {
    @TypeConverter
    fun fromPatch(value: Patch): String {
        val gson = Gson()
        return gson.toJson(value)
    }

    @TypeConverter
    fun toPatch(value: String): Patch {
        val gson = Gson()
        return gson.fromJson(value, Patch::class.java)
    }
}