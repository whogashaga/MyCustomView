package com.kerry.guidelinebubble.guide.model

import android.graphics.RectF
import android.view.View

interface Highlight {

    fun getShape(): Shape

    /**
     * 當shape為CIRCLE时使用此方法取得半徑
     */
    fun getRadius(): Float


    /**
     * 取得圓角，當shape = Shape.ROUND_RECTANGLE才使用此方法
     */
    fun getRound(): Int


    /**
     * 額外参数
     */
    fun getOptions(): HighlightOptions?


    /**
     * @param view anchor view
     * @return highlight's rectF
     */
    fun getRectF(view: View): RectF

    enum class Shape {
        CIRCLE, //圆形
        RECTANGLE, //矩形
        OVAL, //椭圆
        ROUND_RECTANGLE//圆角矩形
    }
}
