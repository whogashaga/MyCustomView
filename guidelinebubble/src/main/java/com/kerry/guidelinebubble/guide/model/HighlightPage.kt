package com.kerry.guidelinebubble.guide.model

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.kerry.guidelinebubble.guide.core.GuideLayout.Companion.DEFAULT_BACKGROUND_COLOR


class HighlightPage @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    lateinit var guidePage: GuidePage
    private var mPaint: Paint = Paint()

    init {
        mPaint.isAntiAlias = true

        mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

        mPaint.maskFilter = BlurMaskFilter(10f, BlurMaskFilter.Blur.INNER)
        setLayerType(View.LAYER_TYPE_HARDWARE, null)

        //ViewGroup預設設定為true，會使onDraw方法不執行
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas?) {
        val backgroundColor = guidePage.getBackgroundColor()
        canvas?.drawColor(if (backgroundColor == 0) DEFAULT_BACKGROUND_COLOR else backgroundColor)
        drawHighlights(canvas)
    }

    private fun drawHighlights(canvas: Canvas?) {
        val highLights = guidePage.highLights
        for (highLight in highLights) {
            val rectF = highLight.getRectF(parent as ViewGroup)
            when (highLight.getShape()) {
                Highlight.Shape.CIRCLE -> canvas?.drawCircle(
                    rectF.centerX(),
                    rectF.centerY(),
                    highLight.getRadius(),
                    mPaint
                )
                Highlight.Shape.OVAL -> canvas?.drawOval(rectF, mPaint)
                Highlight.Shape.ROUND_RECTANGLE -> canvas?.drawRoundRect(
                    rectF,
                    highLight.getRound().toFloat(),
                    highLight.getRound().toFloat(),
                    mPaint
                )
                Highlight.Shape.RECTANGLE -> canvas?.drawRect(rectF, mPaint)
            }
            notifyDrewListener(canvas, highLight, rectF)
        }
    }

    private fun notifyDrewListener(canvas: Canvas?, highLight: Highlight?, rectF: RectF?) {
        val options = highLight?.getOptions()
        if (canvas != null && rectF != null) {
            options?.onHighlightDrawListener?.onHighlightDraw(canvas, rectF)
        }
    }
}