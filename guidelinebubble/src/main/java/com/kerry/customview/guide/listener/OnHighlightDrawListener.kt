package com.kerry.customview.guide.listener

import android.graphics.Canvas
import android.graphics.RectF

interface OnHighlightDrawListener {

    fun onHighlightDraw(canvas: Canvas, rectF: RectF)
}