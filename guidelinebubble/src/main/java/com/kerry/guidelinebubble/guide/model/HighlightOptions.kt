package com.kerry.guidelinebubble.guide.model

import android.view.View
import com.kerry.guidelinebubble.guide.listener.OnHighlightDrawListener

class HighlightOptions {
    lateinit var onClickListener: View.OnClickListener
    lateinit var relativeGuide: RelativeGuide
    var onHighlightDrawListener: OnHighlightDrawListener? = null
    var fetchLocationEveryTime: Boolean = false

    class Builder {

        private val options: HighlightOptions = HighlightOptions()

        /**
         * Highlight click
         */
        fun setOnClickListener(listener: View.OnClickListener): Builder {
            options.onClickListener = listener
            return this
        }

        /**
         * @param relativeGuide Highlight相對位置教學頁
         */
        fun setRelativeGuide(relativeGuide: RelativeGuide): Builder {
            options.relativeGuide = relativeGuide
            return this
        }

        /**
         * @param listener
         */
        fun setOnHighlightDrewListener(listener: OnHighlightDrawListener): Builder {
            options.onHighlightDrawListener = listener
            return this
        }

        fun isFetchLocationEveryTime(b: Boolean): Builder {
            options.fetchLocationEveryTime = b
            return this
        }

        fun build(): HighlightOptions {
            return options
        }
    }

}
