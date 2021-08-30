package com.kerry.guidelinebubble.guide.util

import android.app.Activity
import android.content.Context
import android.graphics.Point

/**
 * Created by hubert
 *
 *
 * Created on 2017/7/27.
 */
class ScreenUtils private constructor() {

    init {
        throw AssertionError()
    }

    companion object {

        /**
         * @param context context
         * @param dp      dp值
         * @return px值
         */
        fun dp2px(context: Context, dp: Int): Int {
            return (dp * context.resources.displayMetrics.density).toInt()
        }

        /**
         * 取得螢幕寬度
         */
        fun getScreenWidth(context: Context): Int {
            val dm = context.resources.displayMetrics
            return dm.widthPixels
        }

        /**
         * 取得螢幕高度
         */
        fun getScreenHeight(context: Context): Int {
            val dm = context.resources.displayMetrics
            return dm.heightPixels
        }

        /**
         * 狀態欄高度
         */
        fun getStatusBarHeight(context: Context): Int {
            // 一般是25dp
            var height = dp2px(context, 20)
            LogUtil.i("common statusBar height:$height")
            //取得status_bar_height id
            val resourceId =
                context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                height = context.resources.getDimensionPixelSize(resourceId)
                LogUtil.i("real statusBar height:$height")
            }
            LogUtil.i("finally statusBar height:$height")
            return height
        }

        /**
         * 是否顯示Navigation Bar
         */
        fun isNavigationBarShow(activity: Activity): Boolean {
                val display = activity.windowManager.defaultDisplay
                val size = Point()
                val realSize = Point()
                display.getSize(size)
                display.getRealSize(realSize)
                return realSize.y != size.y
        }

        /**
         * 取得Navigation Bar 高度
         */
        fun getNavigationBarHeight(activity: Activity): Int {
            if (!isNavigationBarShow(activity))
                return 0
            var height = 0
            val resources = activity.resources
            //取得Navigation Bar 高度
            val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
            if (resourceId > 0)
                height = resources.getDimensionPixelSize(resourceId)
            LogUtil.i("NavigationBar的高度:$height")
            return height
        }
    }
}
