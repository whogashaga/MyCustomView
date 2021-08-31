package com.kerry.customview.guide.listener

import android.view.View
import com.kerry.customview.guide.core.Controller


interface OnLayoutInflatedListener {

    /**
     * @param view       [com.kerry.customview.guide.model.GuidePage.setLayoutRes]
     * @param controller [Controller]
     */
    fun onLayoutInflated(view: View, controller: Controller)
}
