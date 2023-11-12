package com.example.arkanoid

import android.graphics.RectF
import kotlin.random.Random

class Ball(screenX: Int, screenY: Int) {

    val ballWidth: Float = 20f
    val ballHeight: Float = 20f

    var xVelocity: Float
    var yVelocity: Float

    private val rect: RectF

    init {
        xVelocity = 200f
        yVelocity = -300f
        rect = RectF()

    }

    fun getRectF(): RectF {
        return rect
    }

    fun update(fps: Long) {
        rect.left = rect.left + (xVelocity / fps)
        rect.top = rect.top + (yVelocity / fps)
        rect.right = rect.left + ballWidth
        rect.bottom = rect.top - ballHeight
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
        rect.bottom = y + ballHeight
        rect.top = y
    }

    fun clearObstacleX(x: Float) {
        rect.left = x
        rect.right = x + ballWidth
    }

    fun reset(x: Int, y: Int) {
        rect.left = x / 2f
        rect.top = y - 20f
        rect.right = x / 2 + ballWidth;
        rect.bottom = y - 20 - ballHeight;
    }
}