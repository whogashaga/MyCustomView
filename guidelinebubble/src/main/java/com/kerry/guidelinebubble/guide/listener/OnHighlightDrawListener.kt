package com.kerry.guidelinebubble.guide.listener

import android.graphics.Canvas
import android.graphics.RectF

interface OnHighlightDrawListener {

    fun onHighlightDraw(canvas: Canvas, rectF: RectF)
}