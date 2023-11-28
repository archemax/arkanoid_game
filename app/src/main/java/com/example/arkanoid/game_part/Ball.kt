package com.example.arkanoid.game_part

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.example.arkanoid.R

import kotlin.random.Random

class Ball(private val context: Context, screenX: Int, screenY: Int) {

    private val ballBitmap: Bitmap = BitmapFactory.decodeResource(
        context.resources, R.drawable.red_ball,
        )

    val ballWidth: Float = 40f
    val ballHeight: Float = 40f

    var xVelocity: Float
        private set

    var yVelocity: Float
        private set

    private val rect: RectF

    init {
        xVelocity = 250f
        yVelocity = -400f
        rect = RectF()
    }

    fun setXVelocity(x: Float) {
        this.xVelocity = x
    }

    fun setYVelocity(y: Float) {
        this.yVelocity = y
    }

    fun getHeight(): Float {
        return ballHeight
    }

    fun setY(y: Float) {
        rect.top = y
        rect.bottom = y + ballHeight
    }

    fun getRectF(): RectF {
        return rect
    }

    fun update(fps: Long) {
        rect.left = rect.left + (xVelocity / fps)
        rect.top = rect.top + (yVelocity / fps)
        rect.right = rect.left + ballWidth
        rect.bottom = rect.top + ballHeight
    }

    fun reverseXVelocity() {
        xVelocity = -xVelocity
    }

    fun reverseYVelocity() {
        yVelocity = -yVelocity
    }

    fun setRandomVelocity() {
        val generator = Random(20)
        val answer = generator.nextInt(2)

        if (answer == 0) {
            reverseXVelocity()
        }

    }

    fun clearObstacleY(y: Float) {
        rect.top = y
        rect.bottom = y + 40f

    }

    fun clearObstacleX(x: Float) {
        rect.left = x
        rect.right = x + ballWidth
    }

    fun reset(x: Int, y: Int) {
        rect.left = (x / 2f) - (ballWidth / 2f)
        rect.top = y / 2f
        rect.right = (x / 2f) + (ballWidth / 2f)
        rect.bottom = (y / 2f) + ballHeight
    }

    fun draw(canvas: Canvas, paint: Paint) {
        canvas.drawBitmap(ballBitmap, null, rect, paint)
    }
}