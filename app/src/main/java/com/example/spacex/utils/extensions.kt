package com.example.spacex.utils

import java.text.SimpleDateFormat
import java.util.*

// Taking in a Unix time and formatting it to a date as String
fun Long.fromUnixToFormatted(): String {
    val sdf = SimpleDateFormat("dd/MM/yy    hh:mm")
    val netDate = Date(this * 1000)
    return sdf.format(netDate)
}

