package com.example.arkanoid

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent

import android.view.SurfaceHolder
import android.view.SurfaceView


class BreakoutGame : Activity() {
    lateinit var breakoutView: BreakoutView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        breakoutView = BreakoutView(context = this)
        setContentView(breakoutView)
    }


    protected override fun onResume() {
        super.onResume()
        breakoutView.resume()
    }


    protected override fun onPause() {
        super.onPause()

        // Tell the gameView pause method to execute
        breakoutView.pause()
    }


}

