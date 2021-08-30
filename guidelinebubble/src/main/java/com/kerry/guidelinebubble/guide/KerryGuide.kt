package com.kerry.guidelinebubble.guide

import android.app.Activity

import androidx.fragment.app.Fragment
import com.kerry.guidelinebubble.guide.core.Builder


object KerryGuide {

    const val TAG = "KerryGuide"

    /**
     * NewbieGuide入口
     *
     * @param activity activity
     * @return builder class
     */
    fun with(activity: Activity): Builder {
        return Builder(activity)
    }

    fun with(fragment: Fragment): Builder {
        return Builder(fragment)
    }
}

