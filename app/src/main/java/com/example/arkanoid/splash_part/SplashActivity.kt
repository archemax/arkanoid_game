package com.example.arkanoid.splash_part

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.annotation.RequiresExtension
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.arkanoid.MainActivity
import com.example.arkanoid.R
import com.example.arkanoid.utils.FullScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var sharedPrefsClass: SharedPrefsClass

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onCreate(savedInstanceState: Bundle?) {
//        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_avtivity)
        FullScreen.fullScreen(this)
        FullScreen.enableFullScreen(this)

        val progressBar: ProgressBar = findViewById(R.id.indeterminateBar)
        val viewModel: SplashViewModel by viewModels()

        sharedPrefsClass = SharedPrefsClass(this)

        lifecycleScope.launch {
            delay(50)
            progressBar.isIndeterminate

            // check if there is smth in the shared prefs
            val hasStatus = sharedPrefsClass.getStatus()

            if (hasStatus) {
                //open web view
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            } else {
                //make api call
                viewModel.statusResponse.observe(
                    this@SplashActivity
                ) { statusResponse ->
                    val status = statusResponse?.data ?: false
                    if (status) {
                        sharedPrefsClass.saveStatus(status)
                            //startActivity(Intent(this@SplashActivity, WebViewActivity::class.java))
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    } else {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    }


                }
                //if api call is true -> write true ti SharPrefs and open webView
                //if api call false -> do not write anytheinf and open the game
            }

//        else if (!hasStatus) {
//            //open the game, DO NOT make Api call
//            Intent(this@SplashActivity, MainActivity::class.java)
//        }


        }
    }

    // back button disabled
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
    }
}

