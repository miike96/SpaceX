package com.mikeschvedov.spacex.utils.helper_classes

import android.util.Log
import com.mikeschvedov.spacex.BuildConfig

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