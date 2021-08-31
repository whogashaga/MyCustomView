package com.kerry.customview.guide.core

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.kerry.customview.guide.KerryGuide
import com.kerry.customview.guide.listener.AnimationListenerAdapter
import com.kerry.customview.guide.model.GuidePage
import com.kerry.customview.guide.model.Highlight
import kotlin.math.abs

class GuideLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var controller: Controller
    private lateinit var guidePage: GuidePage
    private lateinit var listener: OnGuideLayoutDismissListener
    private var downX: Float = 0.toFloat()
    private var downY: Float = 0.toFloat()
    private var touchSlop: Int = 0

    constructor(
        context: Context,
        page: GuidePage,
        controller: Controller
    ) : this(context = context) {
        init()
        setGuidePage(page)
        this.controller = controller
    }

    private fun init() {
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    private fun setGuidePage(page: GuidePage) {
        this.guidePage = page
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                val upX = event.x
                val upY = event.y
                if (abs(upX - downX) < touchSlop && abs(upY - downY) < touchSlop) {
                    val highLights = guidePage.getHighLights()
                    for (highLight in highLights) {
                        val rectF = highLight.getRectF(parent as ViewGroup)
                        if (rectF.contains(upX, upY)) {
                            notifyClickListener(highLight)
                            return true
                        }
                    }
                    performClick()
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun notifyClickListener(highlight: Highlight) {
        val options = highlight.getOptions()
        options?.onClickListener?.onClick(this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        addCustomToLayout(guidePage)
        val enterAnimation = guidePage.getEnterAnimation()
        if (enterAnimation != null) {
            startAnimation(enterAnimation)
        }
    }

    private fun addCustomToLayout(guidePage: GuidePage) {
        removeAllViews()
        val layoutResId = guidePage.layoutResId
        if (layoutResId != 0) {
            val view = LayoutInflater.from(context).inflate(layoutResId, this, false)
            val params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
            val viewIds = guidePage.clickToDismissIds
            if (viewIds != null && viewIds.isNotEmpty()) {
                for (viewId in viewIds) {
                    val click = view.findViewById<View>(viewId)
                    click?.setOnClickListener { remove() }
                        ?: Log.w(
                            KerryGuide.TAG,
                            "can't find the view by id : $viewId which used to remove guide page"
                        )
                }
            }
            val inflatedListener = guidePage.getOnLayoutInflatedListener()
            inflatedListener?.onLayoutInflated(view, controller)
            addView(view, params)
        }
        val relativeGuides = guidePage.relativeGuides
        if (relativeGuides.isNotEmpty()) {
            for (relativeGuide in relativeGuides) {
                addView(relativeGuide.getGuideLayout(parent as ViewGroup, controller))
            }
        }
    }

    fun setOnGuideLayoutDismissListener(listener: OnGuideLayoutDismissListener) {
        this.listener = listener
    }

    fun remove() {
        if (guidePage.getEnterAnimation()?.hasEnded() != true) return
        if (guidePage.getExitAnimation()?.hasStarted() == true) return
        val exitAnimation = guidePage.getExitAnimation()
        if (exitAnimation != null) {
            exitAnimation.setAnimationListener(object : AnimationListenerAdapter() {
                override fun onAnimationEnd(animation: Animation) {
                    dismiss()
                }
            })
            startAnimation(exitAnimation)
        } else {
            dismiss()
        }
    }

    private fun dismiss() {
        if (parent != null) {
            (parent as ViewGroup).removeView(this)
            if (::listener.isInitialized) {
                listener.onGuideLayoutDismiss(this)
            }
        }
    }

    interface OnGuideLayoutDismissListener {
        fun onGuideLayoutDismiss(guideLayout: GuideLayout)
    }

    companion object {
        const val DEFAULT_BACKGROUND_COLOR = -0x4e000000
    }
}
