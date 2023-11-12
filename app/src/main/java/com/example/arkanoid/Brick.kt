package com.example.arkanoid

import android.graphics.RectF

class Brick(row: Int, column: Int, width: Int, height: Int) {

    val rectF: RectF
    private var isVisible: Boolean

    init {
        isVisible = true
        val padding = 2
        rectF = RectF(
            (column * width + padding).toFloat(),
            (row * height + padding).toFloat(),
            (column * width + width - padding).toFloat(),
            (row * height + height - padding).toFloat()
        )
    }

    fun getRect():RectF{
        return this.rectF
    }

    fun setInvisible(){
        isVisible = false
    }

    fun getVisibility():Boolean{
        return isVisible
    }
}