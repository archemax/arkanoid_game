package com.example.arkanoid

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.RectF
import android.media.SoundPool
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.window.layout.WindowMetricsCalculator


class BreakoutView(context: Context) : SurfaceView(context), Runnable {
    var screenX: Int = 0
    var screenY: Int = 0

    var gameThread: Thread? = null
    var ourHolder: SurfaceHolder = holder
    var paddle: Paddle
    var ball: Ball
    var bricks = Array<Brick>(200) { Brick(0, 0, 0, 0) }
    var numberOfBricks: Int = 0

    //sounds and score
//    var soundPool: SoundPool? = null
//    val beep1ID: Int = -1
//    val beep2ID: Int = -1
//    val beep3ID: Int = -1
//    val loseLifeID: Int = -1
//    val explodeID: Int = -1

    // The score
    var score: Int = 0

    // Lives
    var lives: Int = 3

    init {
        var size = Point()
        var display = WindowMetricsCalculator.getOrCreate()
            .computeCurrentWindowMetrics(context)
        screenX = display.bounds.width() - getNavigationBarWidth()
        screenY = display.bounds.height()

        paddle = Paddle(screenX, screenY)
        ball = Ball(screenX = screenX, screenY = screenY)
        createBricksAndRestart()
    }


    // subtract the van bar wirth
    @SuppressLint("InternalInsetResource")
    private fun getNavigationBarWidth(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun createBricksAndRestart() {
        val brickWidth: Int = screenX / 8;
        val brickHeight: Int = screenY / 10;

        numberOfBricks = 0
        for (column in 0 until 8) {
            for (row in 0 until 2) {
                bricks[numberOfBricks] = Brick(row, column, brickWidth, brickHeight)
                numberOfBricks++
            }
        }

        // put the ball back to the start
        ball.reset(screenX, screenY)

        score = 0
        lives = 3

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
        ball.update(fps)
    }

    // now we draw a new uodated  scene
    fun draw() {

        // make sure our suface in valid
        if (ourHolder.surface.isValid) {

            canvas = ourHolder.lockCanvas()

            // draw background color
            canvas.drawColor(Color.argb(255, 100, 120, 80))
            paint.setColor(Color.argb(255, 255, 255, 255))

            // Draw the paddle
            canvas.drawRect(paddle.getRectF(), paint)

            // Draw the ball
            canvas.drawRect(ball.getRectF(), paint)

            // Draw the bricks
            paint.setColor(Color.argb(255, 249, 129, 0))

            for (i in 0 until numberOfBricks) {
                if (bricks[i].getVisibility()) {

                    val ballCenterX = ball.getRectF().centerX()
                    val ballCenterY = ball.getRectF().centerY()

                    if (ballCenterX >= bricks[i].getRect().left
                        && ballCenterX <= bricks[i].getRect().right
                        && ballCenterY >= bricks[i].getRect().top
                        && ballCenterY <= bricks[i].getRect().bottom
                    )
                    //if (RectF.intersects(bricks[i].getRect(), ball.getRectF()))
                    {
                        bricks[i].setInvisible()
                        ball.reverseYVelocity()
                        score = score + 10
                    }
                    canvas.drawRect(bricks[i].getRect(), paint)
                }
            }
            // Check for ball colliding with paddle
            if (RectF.intersects(paddle.getRectF(), ball.getRectF())) {
                ball.setRandomVelocity();
                ball.reverseYVelocity();
                ball.clearObstacleY(paddle.getRectF().top - 2);///////////////////////// -2

            }

            // And deduct a life
            if (ball.getRectF().bottom > screenY) {
                ball.reverseYVelocity()
                ball.reset(screenX, screenY)
                paused = true
                //ball.clearObstacleY(screenY - 2f)

                // Lose a life
                lives--
                if (lives == 0) {
                    paused = true;

                }

            }

            // Bounce the ball back when it hits the top of screen
            if (ball.getRectF().top < 0) {
                ball.reverseYVelocity()
                ball.clearObstacleY(12f)

            }

            // If the ball hits left wall bounce
            if (ball.getRectF().left < 0) {
                ball.reverseXVelocity();
                ball.clearObstacleX(2f);
            }
            // If the ball hits right wall bounce
            if (ball.getRectF().right > screenX) {
                ball.reverseXVelocity();
                ball.clearObstacleX(screenX - 22f)

            }
            //Pause if cleared screen
            if (score == numberOfBricks * 10) {
                paused = true

            }


            paint.setColor(Color.argb(255, 255, 255, 255));
            paint.setTextSize(50F)
            canvas.drawText(
                "Score: "
                        + score + "   Lives: " + lives, 10f, 100f, paint
            )


            // Draw everything to the screen
            // draw thw score
            // Has the player cleared the screen?
            if (score == numberOfBricks * 10) {
                paint.setTextSize(90f)
                canvas.drawText("YOU HAVE WON!", 10f, screenY / 2f, paint)
            }

            // Has the player lost?
            if (lives == 0) {
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(50f)
                canvas.drawText("YOU HAVE LOST!", 10f, screenY / 2f, paint)


                Log.d("youHaveLost", "$lives")
            }
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

                if (lives == 0 || score == numberOfBricks * 10) {
                    createBricksAndRestart()
                    paused = true
                    return true
                }
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
