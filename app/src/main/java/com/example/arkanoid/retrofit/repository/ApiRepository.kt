package com.example.arkanoid.retrofit.repository

import android.net.http.HttpException
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import com.example.arkanoid.retrofit.ApiService
import com.example.arkanoid.retrofit.TestDataDto
import java.io.IOException

class ApiRepository(private val apiService: ApiService) {

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    suspend fun checkStatus(): TestDataDto? {
        Log.d("mylog", "Request")
        return try {
            val response = apiService.checkStatus()
            response
        } catch (e: HttpException) {
            Log.e("SplashViewModel", "HttpException: ${e.message}")
            null
        } catch (e: IOException) {
            Log.e("SplashViewModel", "IOException: ${e.message}")
            null
        } catch (e: Exception) {
            Log.e("SplashViewModel", "Exception: ${e.message}")
            null
        }


    }

}

