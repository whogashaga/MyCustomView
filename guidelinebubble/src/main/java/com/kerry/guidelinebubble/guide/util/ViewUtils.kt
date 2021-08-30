package com.kerry.guidelinebubble.guide.util

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import androidx.viewpager.widget.ViewPager

/**
 * Created by zhy on 15/10/8.
 */
object ViewUtils {
    private const val FRAGMENT_CON = "NoSaveStateFrameLayout"

    fun getLocationInView(parent: View, child: View): Rect {

        var decorView: View? = null
        val context = child.context
        if (context is Activity) {
            decorView = context.window.decorView
        }

        val result = Rect()
        val tmpRect = Rect()

        var tmp = child

        if (child === parent) {
            child.getHitRect(result)
            return result
        }
        while (tmp !== decorView && tmp !== parent) {
            LogUtil.i("tmp class:" + tmp.javaClass.simpleName)
            tmp.getHitRect(tmpRect)
            LogUtil.i("tmp hit Rect:$tmpRect")
            if (tmp.javaClass.name != FRAGMENT_CON) {
                result.left += tmpRect.left
                result.top += tmpRect.top
            }

            if (tmp.parent != null) {
                tmp = tmp.parent as View
            } else {
                throw IllegalArgumentException("the view is not showing in the window!")
            }

            //修正ScrollView取得的位置
            if (tmp.parent is ScrollView) {
                val scrollView = tmp.parent as ScrollView
                val scrollY = scrollView.scrollY
                LogUtil.i("scrollY:$scrollY")
                result.top -= scrollY
            }

            if (tmp.parent is HorizontalScrollView) {
                val horizontalScrollView = tmp.parent as HorizontalScrollView
                val scrollX = horizontalScrollView.scrollX
                LogUtil.i("scrollX:$scrollX")
                result.left -= scrollX
            }

            //added by isanwenyu@163.com fix bug #21 the wrong rect user will receive in ViewPager
            if (tmp.parent != null && tmp.parent is ViewPager) {
                tmp = tmp.parent as View
            }
        }
        result.right = result.left + child.measuredWidth
        result.bottom = result.top + child.measuredHeight
        return result
    }
}
