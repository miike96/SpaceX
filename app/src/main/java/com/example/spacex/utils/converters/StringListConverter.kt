package com.example.spacex.utils.converters

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/*
  In order to use the Json serialization, add these:
  *
  ----- In project Grade: -----
   1) classpath "org.jetbrains.kotlin:kotlin-serialization:1.7.10"
   ----- In module Grade: -----
   2) id 'kotlinx-serialization'
   3) implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0-RC")
 */

class StringListConverter {
    @TypeConverter
    fun fromList(value: List<String>) = Json.encodeToString(value)

    @TypeConverter
    fun toList(value: String) = Json.decodeFromString<List<String>>(value)
}

