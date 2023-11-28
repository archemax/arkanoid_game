package com.example.arkanoid

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.arkanoid.game_part.BreakoutViewGame
import com.example.arkanoid.utils.FullScreen
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    var breakoutView: BreakoutViewGame? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FullScreen.fullScreen(this)

        breakoutView = BreakoutViewGame(this)
        setContentView(breakoutView)


    }

    override fun onResume() {
        super.onResume()
        breakoutView?.resume()
    }


    override fun onPause() {
        super.onPause()
        breakoutView?.pause()
    }


}

