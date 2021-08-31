package com.kerry.customview.guide.model

import android.view.View
import android.view.animation.Animation
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import com.kerry.customview.guide.listener.OnHighlightDrawListener
import com.kerry.customview.guide.listener.OnLayoutInflatedListener
import java.util.*

class GuidePage {

    val highLights = ArrayList<Highlight>()
    private var everywhereCancelable = true
    private var backgroundColor: Int = 0

    var layoutResId: Int = 0
        private set
    var clickToDismissIds: IntArray? = null
        private set
    private var onLayoutInflatedListener: OnLayoutInflatedListener? = null
    private val onHighlightDrawListener: OnHighlightDrawListener? = null
    private var enterAnimation: Animation? = null
    private var exitAnimation: Animation? = null

    val isEmpty: Boolean
        get() = layoutResId == 0 && highLights.size == 0

    val relativeGuides: List<RelativeGuide>
        get() {
            val relativeGuides = ArrayList<RelativeGuide>()
            for (highLight in highLights) {
                val options = highLight.getOptions()
                if (options != null) {
                    relativeGuides.add(options.relativeGuide)
                }
            }
            return relativeGuides
        }

    /**
     * @param view          需要Highlight 的view
     * @param shape         Highlight形状[Highlight.Shape]
     * @param round         Radius size，單位dp，[Highlight.Shape.ROUND_RECTANGLE]有效
     * @param padding       Highlight相對view的padding,單位dp
     * @param relativeGuide 相對Highlight的教學頁
     */
    @JvmOverloads
    fun addHighLight(
        view: View?,
        shape: Highlight.Shape = Highlight.Shape.RECTANGLE,
        round: Int = 0,
        padding: Int = 0,
        relativeGuide: RelativeGuide
    ): GuidePage {
        view?.let {
            val highlight = HighlightView(it, shape, round, padding)
            relativeGuide.highlight = highlight
            highlight.setOptions(HighlightOptions.Builder().setRelativeGuide(relativeGuide).build())
            highLights.add(highlight)
        }
        return this
    }

    /**
     * 加入教學頁layout
     *
     * @param resId layout id
     * @param id    layout中點擊dismiss教學頁的view id
     */
    fun setLayoutRes(@LayoutRes resId: Int, vararg id: Int): GuidePage {
        this.layoutResId = resId
        clickToDismissIds = id
        return this
    }

    fun setEverywhereCancelable(everywhereCancelable: Boolean): GuidePage {
        this.everywhereCancelable = everywhereCancelable
        return this
    }

    /**
     * 設定背景顏色
     */
    fun setBackgroundColor(@ColorInt backgroundColor: Int): GuidePage {
        this.backgroundColor = backgroundColor
        return this
    }

    /**
     * @param onLayoutInflatedListener listener
     */
    fun setOnLayoutInflatedListener(onLayoutInflatedListener: OnLayoutInflatedListener): GuidePage {
        this.onLayoutInflatedListener = onLayoutInflatedListener
        return this
    }

    /**
     * 設定進入動畫
     */
    fun setEnterAnimation(enterAnimation: Animation): GuidePage {
        this.enterAnimation = enterAnimation
        return this
    }

    /**
     * 設定退出動畫
     */
    fun setExitAnimation(exitAnimation: Animation): GuidePage {
        this.exitAnimation = exitAnimation
        return this
    }

    fun isEverywhereCancelable(): Boolean {
        return everywhereCancelable
    }

    fun getHighLights(): List<Highlight> {
        return highLights
    }

    fun getBackgroundColor(): Int {
        return backgroundColor
    }

    fun getOnLayoutInflatedListener(): OnLayoutInflatedListener? {
        return onLayoutInflatedListener
    }

    fun getEnterAnimation(): Animation? {
        return enterAnimation
    }

    fun getExitAnimation(): Animation? {
        return exitAnimation
    }

    companion object {

        fun newInstance(): GuidePage {
            return GuidePage()
        }
    }
}
