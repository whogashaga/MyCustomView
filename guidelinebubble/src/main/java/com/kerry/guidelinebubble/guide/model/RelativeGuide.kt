package com.kerry.guidelinebubble.guide.model

import android.graphics.RectF
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import com.kerry.guidelinebubble.guide.core.Controller
import com.kerry.guidelinebubble.guide.util.LogUtil
import com.kerry.guidelinebubble.guide.util.ScreenUtils

/**
 * Created by hubert on 2018/6/28.
 */
open class RelativeGuide {

    var highlight: Highlight? = null

    @LayoutRes
    var layout: Int = 0
    var view: View? = null
    private var padding: Int = 0
    private var gravity: Int = 0
    private var gravity2: Int = 0
    private var positionOffset = 0

    class MarginInfo {
        var leftMargin: Int = 0
        var topMargin: Int = 0
        var rightMargin: Int = 0
        var bottomMargin: Int = 0
        var gravity: Int = 0


        override fun toString(): String {
            return "MarginInfo{" +
                    "leftMargin=" + leftMargin +
                    ", topMargin=" + topMargin +
                    ", rightMargin=" + rightMargin +
                    ", bottomMargin=" + bottomMargin +
                    ", gravity=" + gravity +
                    '}'.toString()
        }
    }

    /**
     * @param layout  相对位置引导布局
     * @param gravity 仅限left top right bottom
     * @param padding 与高亮view的padding，单位px(傳入dp)
     */
    constructor(
        @LayoutRes layout: Int,
        gravity: Int,
        gravity2: Int,
        guidePositionOffset: Int,
        padding: Int
    ) {
        this.layout = layout
        this.gravity = gravity
        this.gravity2 = gravity2
        this.padding = padding
        this.positionOffset = guidePositionOffset
    }

    constructor(view: View, gravity: Int, gravity2: Int, guidePositionOffset: Int, padding: Int) {
        this.view = view
        this.gravity = gravity
        this.gravity2 = gravity2
        this.padding = padding
        this.positionOffset = guidePositionOffset
    }

    fun getGuideLayout(viewGroup: ViewGroup, controller: Controller): View {
        val layoutParams: FrameLayout.LayoutParams

        if (view != null) {
            onLayoutInflated(view, controller)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        } else {
            view = LayoutInflater.from(viewGroup.context).inflate(layout, viewGroup, false)
            onLayoutInflated(view, controller)
            layoutParams = view?.layoutParams as FrameLayout.LayoutParams

        }
        view?.let {
            val marginInfo = getMarginInfo(gravity, viewGroup, it)
            LogUtil.e(marginInfo.toString())
            offsetMargin(marginInfo, viewGroup, it)
            layoutParams.gravity = marginInfo.gravity
            layoutParams.leftMargin += marginInfo.leftMargin
            layoutParams.topMargin += marginInfo.topMargin
            layoutParams.rightMargin += marginInfo.rightMargin
            layoutParams.bottomMargin += marginInfo.bottomMargin
            view?.layoutParams = layoutParams
        }
        return view as View
    }

    private fun getMarginInfo(
        gravity: Int, viewGroup: ViewGroup,
        view: View
    ): MarginInfo {
        val marginInfo = MarginInfo()
        val dp2px = ScreenUtils.dp2px(view.context, padding)
        view.measure(viewGroup.width, viewGroup.height)
        highlight?.getRectF(viewGroup)?.let {
            when (gravity) {
                Gravity.LEFT, Gravity.START -> {
                    marginInfo.rightMargin = (viewGroup.width - it.left + dp2px).toInt()
                    marginInfo.topMargin = it.top.toInt()
                    marginInfo.leftMargin = dp2px
                }
                Gravity.TOP -> {
                    marginInfo.topMargin = dp2px
                    marginInfo.gravity = Gravity.BOTTOM
                    marginInfo.bottomMargin = (viewGroup.height - it.top + dp2px).toInt()
                    checkAlign(viewGroup, view, marginInfo, it, Gravity.BOTTOM or Gravity.RIGHT)
                }
                Gravity.RIGHT, Gravity.END -> {
                    marginInfo.leftMargin = (it.right + dp2px).toInt()
                    marginInfo.topMargin = it.top.toInt()
                    marginInfo.rightMargin = dp2px
                    marginInfo.bottomMargin = dp2px
                }
                Gravity.BOTTOM -> {
                    marginInfo.topMargin = (it.bottom + dp2px).toInt()
                    marginInfo.bottomMargin = dp2px
                    checkAlign(viewGroup, view, marginInfo, it, Gravity.RIGHT)
                }
            }
        }

        return marginInfo
    }

    private fun checkAlign(
        viewGroup: ViewGroup,
        view: View,
        marginInfo: MarginInfo,
        rectF: RectF,
        i: Int
    ) {
        when (gravity2) {
            Gravity.RIGHT, Gravity.END -> {
                marginInfo.gravity = i
                marginInfo.rightMargin = viewGroup.width - rectF.right.toInt() - ScreenUtils.dp2px(
                    view.context,
                    positionOffset
                )
            }
            Gravity.CENTER_HORIZONTAL -> {

                val autoAlign: Int = if (rectF.right > ScreenUtils.getScreenWidth(view.context)) {
                    ScreenUtils.getScreenWidth(view.context) / 2 - view.measuredWidth / 2
                } else {
                    rectF.left.toInt() + (rectF.right.toInt() - rectF.left.toInt()) / 2 - view.measuredWidth / 2
                }

                if (autoAlign <= 0) {
                    marginInfo.leftMargin = ScreenUtils.dp2px(view.context, 20)
                    marginInfo.rightMargin = ScreenUtils.dp2px(view.context, 20)
                } else {
                    marginInfo.leftMargin = autoAlign
                }
            }
            else -> if (rectF.left.toInt() <= 0) {
                marginInfo.leftMargin = ScreenUtils.dp2px(view.context, 20) + ScreenUtils.dp2px(
                    view.context,
                    positionOffset
                )
                marginInfo.rightMargin = ScreenUtils.dp2px(view.context, 20)
            } else {
                marginInfo.leftMargin =
                    rectF.left.toInt() + ScreenUtils.dp2px(view.context, positionOffset)
                marginInfo.rightMargin = ScreenUtils.dp2px(view.context, padding)
            }
        }
    }

    protected fun offsetMargin(marginInfo: MarginInfo, viewGroup: ViewGroup, view: View) {
        //do nothing
    }

    /**
     * @param view       inflated from layout
     * @param controller controller
     */
    open fun onLayoutInflated(view: View?, controller: Controller) {
        //do nothing
    }
}
