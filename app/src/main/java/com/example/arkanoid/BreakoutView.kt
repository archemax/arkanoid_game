package com.example.arkanoid

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.window.layout.WindowMetricsCalculator
import androidx.window.layout.WindowMetrics;


class BreakoutView(context: Context) : SurfaceView(context), Runnable {
    var screenX: Int = 0
    var screenY: Int = 0

    var gameThread: Thread? = null
    var ourHolder: SurfaceHolder = holder
    var paddle: Paddle

    init {
        var size = Point()
        var display = WindowMetricsCalculator.getOrCreate()
            .computeCurrentWindowMetrics(context)
        screenX = display.bounds.width()
        screenY = display.bounds.height()

        paddle = Paddle(screenX, screenY)
    }

    @Volatile
    var playing: Boolean = false

    // at the satert game is paused
    var paused: Boolean = true

    lateinit var canvas: Canvas
    var paint = Paint()
    var fps: Long = 0
    private var timeThisFrame: Long = 0
    val point = Point()


    @Override
    override fun run() {
        while (playing) {
            var startFrameTime: Long = System.currentTimeMillis()
            if (!paused) {
                update()
            }
            draw()

            timeThisFrame = System.currentTimeMillis() - startFrameTime
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame
            }
        }
    }

    fun update() {
        paddle.update(fps)


    }

    // now we draw a new uodated  scene
    fun draw() {

        // make sure our suface in valid
        if (ourHolder.surface.isValid) {
            canvas = ourHolder.lockCanvas()

            // draw background color
            canvas.drawColor(Color.argb(100, 100, 120, 80))
            paint.setColor(Color.argb(255, 255, 255, 255))

            // Draw the paddle
            canvas.drawRect(paddle.getRectF(), paint)

            // Draw the ball

            // Draw the bricks

            // Draw the HUD

            // Draw everything to the screen
            ourHolder.unlockCanvasAndPost(canvas)
        }
    }

    // if game activity is pause - shutdown thread
    fun pause() {
        playing = false
        try {
            gameThread?.join()
        } catch (e: InterruptedException) {
            Log.e("ERROR", "joining thread")
        }
    }

    fun resume() {
        playing = true
        gameThread = Thread(this)
        gameThread!!.start()
    }


    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {

        when (motionEvent.getAction() and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                paused = false
                if (motionEvent.x > screenX / 2) {
                    paddle.setMovementState(RIGHT)
                } else {
                    paddle.setMovementState(LEFT)
                }


            }

            MotionEvent.ACTION_UP -> {
                paddle.setMovementState(STOPPED)
            }

        }
        return true
    }


}
