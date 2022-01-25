package com.kerry.customview.guideline

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.kerry.customview.R
import com.kerry.customview.guide.KerryGuide
import com.kerry.customview.guide.core.Controller
import com.kerry.customview.guide.listener.OnGuideChangedListener
import com.kerry.customview.guide.model.GuidePage
import com.kerry.customview.guide.model.Highlight
import com.kerry.customview.guide.model.RelativeGuide


/**
 * 簡易產生FeatureDiscovery並自動記錄SharePreferences
 *
 * powered by [KerryGuide] library
 */
class GuidelineView {

    companion object {

        /**
         * @param onSkip 會自動帶入onRemove
         */
        @JvmStatic
        fun buildAndShow(
            activity: Activity,
            key: String,
            debugMode: Boolean,
            onSkip: () -> Unit = {},
            block: Builder.() -> Unit
        ) {
            if (activity.getSharedPreferences(key, Context.MODE_PRIVATE).getBoolean(key, true) || debugMode) {
                Builder().apply { this.onGuideChangeListener(object :OnGuideChangedListener{
                    override fun onShowed(controller: Controller) {}
                    override fun onRemoved(controller: Controller) { onSkip.invoke() }
                    })
                }.apply(block).buildAndShowClassic(activity)
                activity.getSharedPreferences(key, Context.MODE_PRIVATE).edit().putBoolean(key, false).apply()
            } else {
                onSkip.invoke()
            }
        }

        /**
         * @param onSkip 會自動帶入onRemove
         */
        @JvmStatic
        fun buildAndShow(
            fragment: Fragment,
            key: String,
            debugMode: Boolean,
            onSkip: () -> Unit = {},
            block: Builder.() -> Unit
        ) {
            fragment.activity?.let {
                if (it.getSharedPreferences(key, Context.MODE_PRIVATE).getBoolean(key, true) || debugMode) {
                    Builder().apply { this.onGuideChangeListener(object :OnGuideChangedListener{
                            override fun onShowed(controller: Controller) {}
                            override fun onRemoved(controller: Controller) { onSkip.invoke() }
                        })
                    }.apply(block).buildAndShowClassic(fragment)
                    it.getSharedPreferences(key, Context.MODE_PRIVATE).edit().putBoolean(key, false).apply()
                } else {
                    onSkip.invoke()
                }
            }
        }
    }

    class Builder {
        private var targetView: View? = null
        private var title = ""
        private var subTitle = ""
        private var gravity = Gravity.BOTTOM
        private var gravity2 = Gravity.CENTER_HORIZONTAL
        private var triangleOffset = 0
        private var highlightPadding = 8
        private var guidePadding = 8
        private var enterAnimDuration = 100L
        private var exitAnimDuration = 100L
        private var customIdRes = 0
        private var guidePositionOffset = 0
        private var highlightShape = Highlight.Shape.ROUND_RECTANGLE
        private lateinit var listener: OnGuideChangedListener

        /**
         * 設定目標View
         * @param view 傳入View
         */
        fun targetView(view: View?) {
            targetView = view
        }

        /**
         * 設定標題
         * @param string 標題文字
         */
        fun title(string: String) {
            title = string
        }

        /**
         * 設定子標題
         * @param string 子標題文字
         */
        fun subTitle(string: String) {
            subTitle = string
        }

        /**
         * 設定氣泡相對位置
         * @param gravityId default: [Gravity.BOTTOM]
         */
        fun positionGravity(gravityId: Int) {
            gravity = gravityId
            if (gravityId == Gravity.RIGHT || gravityId == Gravity.LEFT || gravityId == Gravity.START || gravityId == Gravity.END) {
                gravity2 = Gravity.TOP
            }
        }

        /**
         * 設定對齊 (左對齊左)
         * #####氣泡在左右只支援TOP#####
         * @param gravityId default: [Gravity.CENTER_HORIZONTAL]
         */
        fun alignGravity(gravityId: Int) {
            gravity2 = gravityId
        }

        /**
         * 設定對齊偏移
         * @param value 數值(dp)
         */
        fun alignOffset(value: Int) {
            guidePositionOffset = value
        }

        /**
         * 設定highlight項目內邊距
         * @param value 數值(dp)
         */
        fun highlightPadding(value: Int) {
            highlightPadding = value
        }

        /**
         * 設定氣泡距離
         * @param value 數值(dp)
         */
        fun guidePadding(value: Int) {
            guidePadding = value
        }

        /**
         * 設定進入動畫長度
         * @param value milliseconds
         */
        fun enterAnimDuration(value: Long) {
            enterAnimDuration = value
        }

        /**
         * 設定離開動畫長度
         * @param value milliseconds
         */
        fun exitAnimDuration(value: Long) {
            exitAnimDuration = value
        }

        /**
         * 設定三角形偏移量(預設為置中)
         * @param offset 偏移數值(dp)
         */
        fun triangleOffset(offset: Int) {
            triangleOffset = offset
        }

        /**
         * 設定自訂layout取代預設樣式
         */
        fun customView(@IdRes int: Int) {
            customIdRes = int
        }

        /**
         * 設定highlight形狀
         * @param shape default:[Highlight.Shape.ROUND_RECTANGLE]
         */
        fun highlightShape(shape: Highlight.Shape) {
            highlightShape = shape
        }

        /**
         * 設定後會覆蓋onSkip
         */
        fun onGuideChangeListener(guideChangedListener: OnGuideChangedListener) {
            listener = guideChangedListener
        }

        private fun buildGuides(activity: Activity?): GuidePage? {
            //targetView?.parent > recyclerView update
            var tmp = targetView
            val decorView = activity?.window?.decorView ?: return null
            while (tmp != decorView) {
                if (tmp != null) {
                    tmp = tmp.parent as View?
                } else {
                    return null
                }
            }

            //create anim
            val enterAnimation = AlphaAnimation(0f, 1f).apply {
                duration = enterAnimDuration
                fillAfter = true
            }
            val exitAnimation = AlphaAnimation(1f, 0f).apply {
                duration = exitAnimDuration
                fillAfter = true
            }
            //set text
            val view = (View.inflate(activity, R.layout.bubble_layout,null) as LinearLayout).apply {
                findViewById<TextView>(R.id.textView_title).text = title
                findViewById<TextView>(R.id.textView_subtitle).text = subTitle
            }
            (view.getChildAt(0) as GuidelineViewTriangle).apply {
                //設定三角位置
                if (triangleOffset == 0) {
                    setLayoutGravity(gravity2)
                } else {
                    setLayoutGravity(gravity2, triangleOffset)
                }

                when (gravity) {
                    Gravity.TOP -> setDirection(GuidelineViewTriangle.BOTTOM)
                    Gravity.RIGHT, Gravity.END -> setDirection(GuidelineViewTriangle.LEFT)
                    Gravity.LEFT, Gravity.START -> setDirection(GuidelineViewTriangle.RIGHT)
                }
            }

            val guidePage = GuidePage.newInstance()
                .setEnterAnimation(enterAnimation)
                .setExitAnimation(exitAnimation)
                .setEverywhereCancelable(true)

            if (customIdRes == 0) {
                guidePage.addHighLight(
                    targetView,
                    highlightShape,
                    16,
                    highlightPadding,
                    object : RelativeGuide(view, gravity, gravity2, guidePositionOffset, guidePadding) {
                        override fun onLayoutInflated(view: View?, controller: Controller) {
                            view?.findViewById<TextView>(R.id.textView_subtitle)?.setOnClickListener {
                                controller.showNextPage()
                            }
                        }
                    })
            } else {
                guidePage.addHighLight(
                    targetView, highlightShape, 16, highlightPadding,
                    RelativeGuide(
                        customIdRes, gravity, gravity2, guidePositionOffset, guidePadding
                    )
                )
            }
            return guidePage
        }

        private fun buildController(
            builder: com.kerry.customview.guide.core.Builder,
            activity: Activity
        ): Controller? {
            if (::listener.isInitialized) {
                builder.setOnGuideChangedListener(listener)
            }
            if (targetView != null) {
                builder.addGuidePage(buildGuides(activity))
            }
            return builder.build()
        }

        internal fun buildAndShowClassic(activity: Activity) {
            buildController(KerryGuide.with(activity), activity)?.show()
        }

        internal fun buildAndShowClassic(fragment: Fragment) {
            buildController(KerryGuide.with(fragment), fragment.requireActivity())?.show()
        }
    }
}