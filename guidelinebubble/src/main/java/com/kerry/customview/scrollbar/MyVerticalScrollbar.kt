package com.kerry.customview.scrollbar

import android.animation.Animator
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.annotation.StyleableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kerry.customview.R
import com.kerry.customview.databinding.CustomMyScrollbarBinding
import com.kerry.customview.guide.util.ScreenUtils

class MyVerticalScrollbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: CustomMyScrollbarBinding = CustomMyScrollbarBinding.bind(
        inflate(context, R.layout.custom_my_scrollbar, this@MyVerticalScrollbar)
    )
    private var endY = 0f
    private lateinit var onScrollListener: RecyclerView.OnScrollListener
    private var revealDuration = 500L

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.MyVerticalScrollbar, defStyleAttr, 0)
        with(binding.viewThumb) {
            setAttributeDrawable(ta, R.styleable.MyVerticalScrollbar_thumbBackground)
            setAttributeWidth(ta, R.styleable.MyVerticalScrollbar_thumbWidth)
            setAttributeHeight(ta, R.styleable.MyVerticalScrollbar_thumbHeight)
        }
        with(binding.viewTrack) {
            setAttributeDrawable(ta, R.styleable.MyVerticalScrollbar_trackBackground)
            setAttributeWidth(ta, R.styleable.MyVerticalScrollbar_trackWidth)
            setAttributeHeight(ta, R.styleable.MyVerticalScrollbar_trackHeight)
        }
        with(binding.tvScrollbarIndicator) {
            setAttributeDrawable(ta, R.styleable.MyVerticalScrollbar_indicatorBackground)
            setAttributeWidth(ta, R.styleable.MyVerticalScrollbar_indicatorWidth)
            setAttributeHeight(ta, R.styleable.MyVerticalScrollbar_indicatorHeight)
            setTextColor(ta.getColor(R.styleable.MyVerticalScrollbar_indicatorTextColor, ContextCompat.getColor(context, R.color.white)))
            ta.getDimensionPixelSize(R.styleable.MyVerticalScrollbar_indicatorTextSize, -1).let {
                if (it != -1) { setTextSize(TypedValue.COMPLEX_UNIT_SP, it.toFloat()) }
            }
        }
        revealDuration = ta.getInteger(R.styleable.MyVerticalScrollbar_revealDurationAfterStop, 500).toLong()
        ta.recycle()
    }

    fun attachTo(rv: RecyclerView, callback: MyScrollbarCallback) {
        if (::onScrollListener.isInitialized) {
            rv.removeOnScrollListener(onScrollListener)
        }
        onScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (recyclerView.canScrollVertically(-1).not()  // 置頂時, 不顯示數量，顯示 GoTop
                    || newState == RecyclerView.SCROLL_STATE_IDLE        // 不滑動時，不顯示數量，顯示 GoTop
                ) {
                    binding.tvScrollbarIndicator.showOrHideSmoothly(false)
                    (recyclerView.layoutManager as? LinearLayoutManager)?.let { manager ->
                        val isRevealTop = recyclerView.canScrollVertically(-1) && manager.findLastVisibleItemPosition() > 5
                        callback.showBtnGoTop(isRevealTop)
                    }
                } else {                                                  // 滑動時，顯示數量，不顯示 GoTop
                    binding.tvScrollbarIndicator.showOrHideSmoothly(true)
                    callback.showBtnGoTop(false)
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                (recyclerView.layoutManager as? LinearLayoutManager)?.let { manager ->
                    val currentItemNum = manager.findLastVisibleItemPosition()
                    if (currentItemNum <= callback.getTotalItemAmount()) {
                        callback.onCurrentItemCountGet(currentItemNum)
                    }
                }

                val range = rv.computeVerticalScrollRange()
                val maxEndY: Float = (range - rv.measuredHeight).toFloat() + 10
                endY += dy
                val proportion: Float = endY / maxEndY
                val transMaxRange = binding.viewTrack.height - binding.viewThumb.height

                binding.viewThumb.translationY = transMaxRange * proportion
                binding.tvScrollbarIndicator.translationY = transMaxRange * proportion
            }
        }
        rv.addOnScrollListener(onScrollListener)
    }

    fun resetScrollbar() {
        endY = 0f
        binding.viewThumb.translationY = 0f
        binding.tvScrollbarIndicator.translationY = 0f
    }

    fun restoreScrollbarState(lastY: Float) {
        endY = lastY
        binding.viewThumb.translationY = lastY
        binding.tvScrollbarIndicator.translationY = lastY
    }

    fun getCurrentState(): Float = endY

    fun setIndicatorText(txt: String) {
        binding.tvScrollbarIndicator.text = txt
    }

    private fun View.setAttributeDrawable(ta: TypedArray, @StyleableRes attrs: Int) {
        ta.getDrawable(attrs)?.let { drawable -> this.background = drawable }
    }

    private fun View.setAttributeWidth(ta: TypedArray, @StyleableRes attrs: Int) {
        ta.getDimensionPixelSize(attrs, -1).let {
            if (it != -1) {
                this.layoutParams.width = it
            }
        }
    }

    private fun View.setAttributeHeight(ta: TypedArray, @StyleableRes attrs: Int) {
        ta.getDimensionPixelSize(attrs, -1).let {
            if (it != -1) {
                this.layoutParams.height = it
            }
        }
    }

    private fun View.showOrHideSmoothly(isShow: Boolean) {
        if (isShow) {
            visibility = View.VISIBLE
            animate().alpha(1.0f).setDuration(200).setListener(null)
        } else {
            animate().alpha(0.0f).setDuration(revealDuration)
                .setInterpolator(AccelerateInterpolator())
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator?) {}
                    override fun onAnimationCancel(p0: Animator?) {}
                    override fun onAnimationRepeat(p0: Animator?) {}
                    override fun onAnimationEnd(p0: Animator?) {
                        visibility = View.GONE
                    }
                })
        }
    }

    interface MyScrollbarCallback {
        fun getTotalItemAmount(): Int
        fun onCurrentItemCountGet(currentCount: Int)
        fun showBtnGoTop(isShow: Boolean)
    }

}