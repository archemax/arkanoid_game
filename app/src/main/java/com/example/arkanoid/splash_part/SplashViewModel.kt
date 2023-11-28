package com.example.arkanoid.splash_part

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arkanoid.retrofit.TestDataDto
import com.example.arkanoid.retrofit.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val apiRepository: ApiRepository
) : ViewModel() {

    private val _statusResponse = MutableLiveData<TestDataDto?>()
    val statusResponse: MutableLiveData<TestDataDto?> = _statusResponse

    init {
        getStatus()
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun getStatus() {
        viewModelScope.launch {
            try {
                val response = apiRepository.checkStatus()
                val currentStatus = response
                _statusResponse.postValue(currentStatus)
            } catch (e: Exception) {
                Log.e("SplashViewModel", "HttpException: ${e.message}")

            }
        }
    }
}

