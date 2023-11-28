package com.example.arkanoid.game_part

import android.graphics.RectF

class Brick(
    val row: Int,
    column: Int,
    width: Int,
    height: Int
) {

    val rectF: RectF
    private var isVisible: Boolean

    init {
        isVisible = true
        val padding = 4
        rectF = RectF(
            (column * width + padding).toFloat(),
            (row * height + padding).toFloat(),
            (column * width + width - padding).toFloat(),
            (row * height + height - padding).toFloat()
        )
    }

    fun getRect(): RectF {
        return this.rectF
    }

    fun setInvisible() {
        isVisible = false
    }

    fun getVisibility(): Boolean {
        return isVisible
    }
}