package com.kerry.customview.guideline

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.kerry.customview.R
import com.kerry.customview.guide.util.ScreenUtils


class GuidelineViewTriangle @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mColor: Int = 0
    private var mDirection: Int = TOP
    private var mPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    private var mPath = Path()

    init {
        val typedArray =
            context.theme.obtainStyledAttributes(attrs, R.styleable.GuidelineViewTriangle, 0, 0)

        mColor = typedArray.getColor(
            R.styleable.GuidelineViewTriangle_momo_color,
            Color.WHITE
        )

        mDirection =
            typedArray.getInt(R.styleable.GuidelineViewTriangle_momo_direction, mDirection)

        typedArray.recycle()
        mPaint.color = mColor
    }


    companion object {
        const val TOP = 0
        const val BOTTOM = 1
        const val RIGHT = 2
        const val LEFT = 3
    }

    override fun onDraw(canvas: Canvas?) {
        with(mPath) {
            when (mDirection) {
                TOP -> {
                    moveTo(0F, height.toFloat())
                    lineTo(width.toFloat(), height.toFloat())
                    lineTo((width / 2).toFloat(), 0F)
                }
                BOTTOM -> {
                    moveTo(0F, 0F)
                    lineTo((width / 2).toFloat(), height.toFloat())
                    lineTo(width.toFloat(), 0F)
                }
                RIGHT -> {
                    moveTo(0F, 0F)
                    lineTo(0F, height.toFloat())
                    lineTo(width.toFloat(), (height / 2).toFloat())
                }
                LEFT -> {
                    moveTo(0F, (height / 2).toFloat())
                    lineTo(width.toFloat(), height.toFloat())
                    lineTo(width.toFloat(), 0F)
                }
                else -> {

                }
            }
            close()
        }
        canvas?.drawPath(mPath, mPaint)
    }

    fun setLayoutGravity(gravityInt: Int, offset: Int = 0) {
        val tmpHeight = ScreenUtils.dp2px(context, 10)
        val tmpWidth = ScreenUtils.dp2px(context, 20)
        val dp2px = ScreenUtils.dp2px(context, offset)
        with(layoutParams as LinearLayout.LayoutParams) {
            when (gravityInt) {
                Gravity.LEFT, Gravity.START -> leftMargin = dp2px
                Gravity.RIGHT, Gravity.END -> rightMargin = dp2px
                Gravity.TOP, Gravity.BOTTOM -> {
                    topMargin = dp2px
                    height = tmpWidth
                    width = tmpHeight
                }
            }
            gravity = gravityInt
        }
    }

    private fun reOrder(linearLayout: LinearLayout) {

        // get number of children
        val childCount = linearLayout.childCount
        // create array
        val children = arrayOfNulls<View>(childCount)
        // get children of linearLayout
        for (i in 0 until childCount) {
            children[i] = linearLayout.getChildAt(i)
        }
        with(linearLayout) {
            removeAllViews()
            addView(children[1])
            addView(children[0])
        }
    }

    fun setDirection(direction: Int) {
        val linearLayout = rootView as LinearLayout
        mDirection = direction
        when (direction) {
            BOTTOM -> {
                reOrder(linearLayout)
                linearLayout.gravity = Gravity.BOTTOM
            }
            RIGHT -> {
                reOrder(linearLayout)
                linearLayout.orientation = LinearLayout.HORIZONTAL
                linearLayout.gravity = Gravity.RIGHT
            }
            LEFT -> linearLayout.orientation = LinearLayout.HORIZONTAL
        }
    }
}
