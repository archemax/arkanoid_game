package com.example.arkanoid.game_part

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

const val STOPPED = 0
const val LEFT = 1
const val RIGHT = 2

class Paddle(private val screenX: Int, screenY: Int) {
    //size of paddle
    private var length: Float = 380F
    private var height: Float = 20F

    // X is the far left of the rectangle which forms our paddle
    private var x: Float = screenX / 2F

    // Y is the top coordinate
    private var y: Float = screenY - 20F
    private var rectF: RectF = RectF(x, y, x + length, y + height)

    // This will hold the pixels per second speed that the paddle will move
    private var paddleSpeed: Float = 450F


    // Is the paddle moving and in which direction
    private var paddleMoving = STOPPED

    // Left and right rectangles
     var leftRectF: RectF = RectF(x, y, x + length / 2, y + height)
     var rightRectF: RectF = RectF(x + length / 2, y, x + length, y + height)

    //getter method to get the rect
    fun getRectF(): RectF {
        return rectF
    }

    fun setMovementState(state: Int) {
        paddleMoving = state
    }

    fun getMovementState(): Int{
        return paddleMoving
    }

    fun reset() {
        x = screenX / 2F
        rectF.left = x
        rectF.right = x + length

        // Update left and right rectangles
        leftRectF.set(x, y, x + length / 2, y + height)
        rightRectF.set(x + length / 2, y, x + length, y + height)
    }



    // This update method will be called from update in BreakoutView
    // It determines if the paddle needs to move and changes the coordinates
    // contained in rect if necessary

    fun update(fps: Long) {
        if (paddleMoving == LEFT) {
            x = x - paddleSpeed / fps
        }

        if (paddleMoving == RIGHT) {
            x = x + paddleSpeed / fps
        }

        if (x < 0) {
            x = 0f
        }
        if (x + length > screenX) {
            x = (screenX - length).toFloat()
        }

        rectF.left = x;
        rectF.right = x + length;

        // Update left and right rectangles
        leftRectF.set(x, y, x + length / 2, y + height)
        rightRectF.set(x + length / 2, y, x + length, y + height)
    }

    // Draw method to draw the paddles
    fun draw(canvas: Canvas) {
        canvas.drawRect(leftRectF, Paint())
        canvas.drawRect(rightRectF, Paint())
    }


}