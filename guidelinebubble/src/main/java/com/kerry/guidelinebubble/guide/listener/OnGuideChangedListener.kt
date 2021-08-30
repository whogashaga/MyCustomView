package com.kerry.guidelinebubble.guide.listener

import com.kerry.guidelinebubble.guide.core.Controller


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