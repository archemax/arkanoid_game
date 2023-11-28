package com.example.arkanoid.game_part

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.arkanoid.R


class BreakoutViewGame(context: Context) : SurfaceView(context), Runnable {

    private fun printDebugInfo() {
        Log.d("BreakoutView", "screenX: $screenX, screenY: $screenY, bricks: $numberOfBricks")
    }

    fun debugPrintInfo() {
        printDebugInfo()
        // Add other debug information if needed
    }


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

//        val display = WindowMetricsCalculator.getOrCreate()
//            .computeCurrentWindowMetrics(context)
//        val cutout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            WindowInsets.Type.displayCutout()
//        } else 0
//        screenX = display.bounds.width() - cutout
//        screenY = display.bounds.height()


        ///// alternative
        val myDisplayMetrics = resources.displayMetrics
        screenX = myDisplayMetrics.widthPixels
        screenY = myDisplayMetrics.heightPixels

        paddle = Paddle(screenX, screenY)
        ball = Ball(context = context, screenX = screenX, screenY = screenY)
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
            for (row in 0 until 3) {
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
            val startFrameTime: Long = System.currentTimeMillis()
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
    val backgroundImage: Bitmap =
        BitmapFactory.decodeResource(context.resources, R.drawable.y_b_21)
    private val rowColors = arrayOf(
        // colors for rows
        Color.argb(255, 157,101,219),
        Color.argb(255, 195,100,219),
        Color.argb(255, 219,105,124)

    )

    fun draw() {

        printDebugInfo()

        // make sure our suface in valid
        if (ourHolder.surface.isValid) {

            canvas = ourHolder.lockCanvas()

            val playgroundBorder = RectF(0f, 0f, screenX.toFloat(), screenY.toFloat())
            // draw background color only inside the border
            val backgroundColor = Paint()
            backgroundColor.color = Color.argb(255, 100, 120, 80)
            //canvas.drawRect(playgroundBorder, backgroundColor)

            canvas.drawBitmap(backgroundImage, null, playgroundBorder, null)

            // Draw the paddle
            val leftPaddleColor = Paint()
            leftPaddleColor.color = Color.argb(255, 0, 180, 20) // Green color
            canvas.drawRoundRect(paddle.leftRectF, 10f, 10f, leftPaddleColor)

            // Draw the right part of the paddle in another color
            val rightPaddleColor = Paint()
            rightPaddleColor.color = Color.argb(255, 0, 20, 120) // Blue color
            canvas.drawRoundRect(paddle.rightRectF, 10f, 10f, rightPaddleColor)

            // Draw the ball
            ball.draw(canvas, paint)

            // Draw the bricks
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

                    //set the color accordint to the row
                    val rowColor = rowColors[bricks[i].row]
                    val bricksColors  = Paint()
                    bricksColors.color  = rowColor
                    canvas.drawRoundRect(bricks[i].getRect(), 10f, 10f, bricksColors)

                    // draw "10" inside each brick
                    val numberTextPaint = Paint()
                    numberTextPaint.color = Color.BLACK
                    numberTextPaint.textSize = 60f
                    numberTextPaint.textAlign = Paint.Align.CENTER
                    val textHeight = numberTextPaint.descent() - numberTextPaint.ascent()
                    val textOffset = (textHeight / 2) - numberTextPaint.descent()
                    val number10 = "10"
                    canvas.drawText(number10,
                        bricks[i].getRect().centerX(),
                        bricks[i].getRect().centerY()+textOffset,
                        numberTextPaint
                    )
                }
            }

            // Check for ball colliding with paddle
            if (RectF.intersects(paddle.getRectF(), ball.getRectF())) {

                val paddleCenterX = paddle.getRectF().centerX()
                val ballCenterX = ball.getRectF().centerX()

                if (ballCenterX < paddleCenterX) {
                    ball.setXVelocity(-Math.abs(ball.xVelocity))
                } else {
                    ball.setXVelocity(Math.abs(ball.xVelocity))
                }

                ball.setYVelocity(-Math.abs(ball.yVelocity))

                ball.setY(paddle.getRectF().top - ball.getHeight() - 2);


            }

            // And deduct a life
            if (ball.getRectF().bottom > screenY) {
                ball.reverseYVelocity()
                ball.reset(screenX, screenY)
                paddle.reset()
                paused = true

                // Lose a life
                lives--
                if (lives == 0) {
                    paused = true
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
                ball.clearObstacleX(screenX - 42f)

            }
            //Pause if cleared screen
            if (score == numberOfBricks * 10) {
                paused = true

            }

            //define the color and size of text
            val textColorAndSize = Paint()
            textColorAndSize.color = Color.argb(250, 250, 250, 250)
            textColorAndSize.textSize = 50f
            canvas.drawText(
                "Score: "
                        + score + "   Lives: " + lives, 10f, 100f, textColorAndSize
            )
            // Draw everything to the screen
            // draw thw score
            // Has the player cleared the screen?
            if (score == numberOfBricks * 10) {
                canvas.drawText("YOU HAVE WON!", 10f, screenY / 2f, textColorAndSize)
            }
            // Has the player lost?
            if (lives == 0) {
                canvas.drawText("YOU HAVE LOST!", 10f, screenY / 2f, textColorAndSize)
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
