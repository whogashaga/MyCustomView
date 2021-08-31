package com.kerry.customview.guide.listener

import com.kerry.customview.guide.core.Controller


interface OnGuideChangedListener {
    /**
     * @param controller
     */
    fun onShowed(controller: Controller)

    /**
     * @param controller
     */
    fun onRemoved(controller: Controller)
}