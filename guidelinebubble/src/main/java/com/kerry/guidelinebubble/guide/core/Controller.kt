package com.kerry.guidelinebubble.guide.core

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.kerry.guidelinebubble.guide.lifecycle.FragmentLifecycleAdapter
import com.kerry.guidelinebubble.guide.lifecycle.ListenerFragment
import com.kerry.guidelinebubble.guide.listener.OnGuideChangedListener
import com.kerry.guidelinebubble.guide.listener.OnPageChangedListener
import com.kerry.guidelinebubble.guide.model.GuidePage
import com.kerry.guidelinebubble.guide.model.HighlightPage
import com.kerry.guidelinebubble.guide.util.LogUtil
import java.security.InvalidParameterException

/**
 * guide的控制器
 */
class Controller(builder: Builder) {

    private val activity: Activity = builder.activity
    private val fragment: Fragment? = builder.fragment
    private val onGuideChangedListener: OnGuideChangedListener? = builder.onGuideChangedListener
    private val onPageChangedListener: OnPageChangedListener? = builder.onPageChangedListener
    private val guidePages: List<GuidePage>? = builder.guidePages
    private var current: Int = 0//目前頁
    private var currentLayout: GuideLayout? = null
    private var mParentView: FrameLayout? = null
    private var indexOfChild = -1//使用anchor紀錄在parent layout的位置
    private var isShowing: Boolean = false

    init {

        var anchor: View? = builder.anchor
        if (anchor == null) {
//            anchor = activity?.findViewById(android.R.id.content)
            //覆蓋狀態欄
            anchor = activity.window?.decorView
        }
        if (anchor is FrameLayout) {
            mParentView = anchor
        } else {
            val frameLayout = FrameLayout(activity)
            val parent = anchor?.parent as ViewGroup
            indexOfChild = parent.indexOfChild(anchor)
            parent.removeView(anchor)
            if (indexOfChild >= 0) {
                parent.addView(frameLayout, indexOfChild, anchor.layoutParams)
            } else {
                parent.addView(frameLayout, anchor.layoutParams)
            }
            val lp = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            frameLayout.addView(anchor, lp)
            mParentView = frameLayout
        }
    }

    /**
     * 顯示index layout
     */
    fun show() {

        if (isShowing) {
            return
        }
        isShowing = true
        mParentView?.post {
            if (guidePages.isNullOrEmpty()) {
//                throw IllegalStateException("there is no guide to show!! Please add at least one Page.");
                return@post
            }
            current = 0
            showGuidePage()
            onGuideChangedListener?.onShowed(this@Controller)
            addListenerFragment()
        }
    }

    /**
     * 顯示指定位置引導頁
     *
     * @param position from 0 to (pageSize - 1)
     */
    private fun showPage(position: Int) {
        if (position < 0 || position > guidePages?.size?.minus(1) ?: 0) {
            throw InvalidParameterException(
                "The Guide page position is out of range. current:"
                        + position + ", range: [ 0, " + guidePages?.size + " )"
            )
        }
        if (current == position) {
            return
        }
        current = position
        //fix #59 GuideLayout.setOnGuideLayoutDismissListener() on a null object reference
        if (currentLayout != null) {
            currentLayout?.setOnGuideLayoutDismissListener(object :
                GuideLayout.OnGuideLayoutDismissListener {
                override fun onGuideLayoutDismiss(guideLayout: GuideLayout) {
                    showGuidePage()
                }
            })
            currentLayout?.remove()
        } else {
            showGuidePage()
        }
    }

    /**
     * 顯示前一頁
     */
    fun showPreviouosPage() {
        showPage(--current)
    }

    /**
     * 顯示下一頁
     */
    fun showNextPage() {
        if (guidePages!!.size - 1 > current) {
            showPage(current + 1)
        } else {
            currentLayout?.remove()
        }
    }

    /**
     * 顯示目前index
     */
    private fun showGuidePage() {
        val page = guidePages!![current]
        val guideLayout = GuideLayout(activity, page, this)
        guideLayout.setOnGuideLayoutDismissListener(object :
            GuideLayout.OnGuideLayoutDismissListener {
            override fun onGuideLayoutDismiss(guideLayout: GuideLayout) {
                showNextOrRemove()
            }
        })
        mParentView?.addView(
            guideLayout, FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        val highlightPage = HighlightPage(activity).apply { guidePage = page }
        guideLayout.addView(
            highlightPage, 0, FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        for (highLight in page.highLights) {
            val animation = ScaleAnimation(
                1.0f,
                1.05f,
                1.0f,
                1.05f,
                Animation.ABSOLUTE,
                guideLayout.let { highLight.getRectF(it).centerX() },
                Animation.ABSOLUTE,
                highLight.getRectF(guideLayout).centerY()
            )
            animation.duration = 400
            animation.repeatCount = -1
            animation.repeatMode = Animation.REVERSE
            highlightPage.startAnimation(animation)
        }
        guideLayout.setOnClickListener {
            if (page.isEverywhereCancelable()) {
                guideLayout.remove()
            }
        }
        highlightPage.setOnClickListener {
            if (page.isEverywhereCancelable()) {
                guideLayout.remove()
            }
        }

        currentLayout = guideLayout
        onPageChangedListener?.onPageChanged(current)
        isShowing = true
    }

    private fun showNextOrRemove() {
        if (current < guidePages!!.size - 1) {
            current++
            showGuidePage()
        } else {
            onGuideChangedListener?.onRemoved(this@Controller)
            removeListenerFragment()
            isShowing = false
        }
    }

    /**
     * 中斷index顯示
     */
    fun remove() {
        if (currentLayout?.parent != null) {
            val parent = currentLayout?.parent as ViewGroup
            parent.removeView(currentLayout)
            //移除anchor添加的frameLayout
            if (parent !is FrameLayout) {
                val original = parent.parent as ViewGroup
                val anchor = parent.getChildAt(0)
                parent.removeAllViews()
                if (anchor != null) {
                    if (indexOfChild > 0) {
                        original.addView(anchor, indexOfChild, parent.layoutParams)
                    } else {
                        original.addView(anchor, parent.layoutParams)
                    }
                }
            }
            onGuideChangedListener?.onRemoved(this)
            currentLayout = null
        }
        isShowing = false
    }

    private fun addListenerFragment() {
        if (fragment != null && fragment.isAdded) {
            val fm = fragment.childFragmentManager
            var listenerFragment: ListenerFragment? =
                fm.findFragmentByTag(LISTENER_FRAGMENT) as? ListenerFragment
            if (listenerFragment == null) {
                listenerFragment = ListenerFragment()
                fm.beginTransaction().add(listenerFragment, LISTENER_FRAGMENT)
                    .commitAllowingStateLoss()
            }
            listenerFragment.setFragmentLifecycle(object : FragmentLifecycleAdapter() {
                override fun onDestroyView() {
                    LogUtil.i("ListenerFragment.onDestroyView")
                    remove()
                }
            })
        }
    }

    private fun removeListenerFragment() {
        //隱藏index時移除監聽fragment
        if (fragment != null) {
            val fm = fragment.childFragmentManager
            val listenerFragment = fm.findFragmentByTag(LISTENER_FRAGMENT) as? ListenerFragment
            if (listenerFragment != null)
                fm.beginTransaction().remove(listenerFragment).commitAllowingStateLoss()
        }
    }

    companion object {
        private const val LISTENER_FRAGMENT = "listener_fragment"
    }
}
