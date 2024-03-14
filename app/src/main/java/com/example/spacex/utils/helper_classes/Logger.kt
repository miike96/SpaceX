package com.example.spacex.utils.helper_classes

import android.util.Log
import com.example.spacex.BuildConfig

class Logger {
    companion object{
        fun i(tag: String, message: String){
            if(BuildConfig.DEBUG) Log.e(tag, message)
        }
        fun e(tag: String, message: String){
            if(BuildConfig.DEBUG) Log.e(tag, message)
        }
    }
}