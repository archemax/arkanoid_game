package com.example.arkanoid

import android.graphics.RectF

const val STOPPED = 0
const val LEFT = 1
const val RIGHT = 2
class Paddle(screenX: Int, screenY: Int) {
    //size of paddle
    private var length: Float = 130F
    private var height: Float = 20F

    // X is the far left of the rectangle which forms our paddle
    private var x: Float = screenX / 2F

    // Y is the top coordinate
    private var y: Float = screenY - 20F
    private var rectF: RectF = RectF(x, y, x + length, y + height)

    // This will hold the pixels per second speed that the paddle will move
    private var paddleSpeed: Float = 350F




    // Is the paddle moving and in which direction
    private var paddleMoving = STOPPED

    //gettter method to get the rect
    fun getRectF(): RectF {
        return rectF
    }
    fun setMovementState(state:Int){
        paddleMoving = state
    }


    // This update method will be called from update in BreakoutView
    // It determines if the paddle needs to move and changes the coordinates
    // contained in rect if necessary

    fun update(fps: Long) {
        if (paddleMoving == LEFT) {
            x = x - paddleSpeed / fps;
        }

        if (paddleMoving == RIGHT) {
            x = x + paddleSpeed / fps;
        }

        rectF.left = x;
        rectF.right = x + length;
    }

}