package com.kerry.customview.guide.model

import android.graphics.Rect
import android.graphics.RectF
import android.view.View
import com.kerry.customview.guide.util.LogUtil
import com.kerry.customview.guide.util.ScreenUtils
import kotlin.math.max

class HighlightView(
    private val mHole: View,
    private val shape: Highlight.Shape,
    private val round: Int,
    private val padding: Int
) : Highlight {
    private var options: HighlightOptions? = null
    private var rectF: RectF? = null

    fun setOptions(options: HighlightOptions) {
        this.options = options
    }

    override fun getShape(): Highlight.Shape {
        return shape
    }

    override fun getRound(): Int {
        return round
    }

    override fun getOptions(): HighlightOptions? {
        return options
    }

    override fun getRadius(): Float {
        return (max(mHole.width / 2, mHole.height / 2) + padding).toFloat()
    }

    override fun getRectF(view: View): RectF {
        if (rectF == null) {
            rectF = fetchLocation(view)
        } else if (options?.fetchLocationEveryTime == true) {
            rectF = fetchLocation(view)
        }
        LogUtil.i(mHole.javaClass.simpleName + "'s location:" + rectF)
        return rectF as RectF
    }

    private fun fetchLocation(target: View): RectF {
        val location = RectF()
        val locationInView = Rect()
        mHole.getGlobalVisibleRect(locationInView)
        //左邊界
        if (locationInView.left <= 0) {
            location.left = 10f
        } else {
            location.left = (locationInView.left - padding).toFloat()
        }
        location.top = (locationInView.top - padding).toFloat()
        //右邊界
        if (locationInView.right >= ScreenUtils.getScreenWidth(target.context)) {
            location.right = (ScreenUtils.getScreenWidth(target.context) - 10).toFloat()
        } else {
            location.right = (locationInView.right + padding).toFloat()
        }
        location.bottom = (locationInView.bottom + padding).toFloat()
        return location
    }

}