package com.kerry.guidelinebubble.guide.listener

import android.view.View
import com.kerry.guidelinebubble.guide.core.Controller


interface OnLayoutInflatedListener {

    /**
     * @param view       [com.kerry.guidelinebubble.guide.model.GuidePage.setLayoutRes]
     * @param controller [Controller]
     */
    fun onLayoutInflated(view: View, controller: Controller)
}
