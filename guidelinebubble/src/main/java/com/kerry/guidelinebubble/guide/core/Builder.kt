package com.kerry.guidelinebubble.guide.core

import android.app.Activity
import android.view.View
import androidx.fragment.app.Fragment
import com.kerry.guidelinebubble.guide.listener.OnGuideChangedListener
import com.kerry.guidelinebubble.guide.listener.OnPageChangedListener
import com.kerry.guidelinebubble.guide.model.GuidePage

import java.util.*

class Builder {
    var activity: Activity
    var fragment: Fragment? = null
    var anchor: View? = null//錨點view
    private var showCounts = 1//顯示次數 default once
    var onGuideChangedListener: OnGuideChangedListener? = null
    var onPageChangedListener: OnPageChangedListener? = null
    var guidePages: MutableList<GuidePage> = ArrayList()

    constructor(activity: Activity) {
        this.activity = activity
    }

    constructor(fragment: Fragment) {
        this.fragment = fragment
        this.activity = fragment.requireActivity()
    }

    /**
     * 教學頁顯示的錨點，即root layout，預設是decorView
     *
     * @param anchor root
     */
    fun anchor(anchor: View): Builder {
        this.anchor = anchor
        return this
    }

    /**
     * 教學頁的顯示次数，預設是1次。
     *
     * @param count 次数
     */
    fun setShowCounts(count: Int): Builder {
        this.showCounts = count
        return this
    }

    /**
     * 新增教學頁
     */
    fun addGuidePage(page: GuidePage?): Builder {
        page?.let { guidePages.add(it) }
        return this
    }

    /**
     * 設定教學頁顯示裝態監聽
     */
    fun setOnGuideChangedListener(listener: OnGuideChangedListener): Builder {
        onGuideChangedListener = listener
        return this
    }

    /**
     * 設定教學頁切換監聽
     */
    fun setOnPageChangedListener(onPageChangedListener: OnPageChangedListener): Builder {
        this.onPageChangedListener = onPageChangedListener
        return this
    }

    /**
     * @return controller
     */
    fun build(): Controller {
        check()
        return Controller(this)
    }

    /**
     * @return controller
     */
    fun show(): Controller {
        check()
        val controller = Controller(this)
        controller.show()
        return controller
    }

    private fun check() {
        check(!(activity == null && (fragment != null || fragment != null))) { "activity is null, please make sure that fragment is showing when call NewbieGuide" }
    }
}