package com.example.arkanoid.splash_part

import android.content.Context
import android.content.SharedPreferences

class SharedPrefsClass(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("my_key", Context.MODE_PRIVATE)

    fun saveStatus(status: Boolean) {
        sharedPreferences
            .edit()
            .putBoolean("status", status)
            .apply()
    }

    fun getStatus(): Boolean {
        return sharedPreferences.getBoolean("status", false)
    }


}