package com.example.arkanoid

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
    private var generalScreenX: Int = 0
    private var generalScreenY: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        breakoutView = BreakoutView(
            context = this,
            myScreenX = generalScreenX,
            myScreenY = generalScreenY
        )

        setContentView(breakoutView)


        val display = windowManager.defaultDisplay
        Log.d("display", "$display") //ok
        val size = Point()
        Log.d("size", "$size")
        display.getSize(size)
        generalScreenX = size.x
        generalScreenY = size.y

        Log.d("ScreenXScreeY", "$generalScreenX,$generalScreenY") //ok


    }


    class BreakoutView(
        context: Context,
        private val myScreenX: Int,
        private val myScreenY: Int
    ) : SurfaceView(context), Runnable {


        var paddle = Paddle(myScreenX, myScreenY)
        var gameThread: Thread? = null
        var ourHolder: SurfaceHolder = holder

//        val myscreenX = this.screenX
//        val myscreenY = this.screenY
        //Log.d ("ScreenXScreeYBreakout", "$myScreenX,$myScreenY")

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
            canvas.drawRect(paddle.getRectF(), paint)

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
                    if (motionEvent.x > myScreenX / 2) {
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

    ////// This is the end of our BreakoutView inner class////////////////////////////////////////////////////////////////////
// This method executes when the player starts the game
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

