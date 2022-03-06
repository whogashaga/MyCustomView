package com.kerry.mycustomview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.kerry.customview.guideline.GuidelineView
import com.kerry.customview.scrollbar.MyVerticalScrollbar
import com.kerry.customview.scrollbar.MyVerticalScrollbar.MyScrollbarCallback
import com.kerry.customview.scrollbar.extension.setContentView
import com.kerry.customview.scrollbar.extension.viewBinding
import com.kerry.mycustomview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)

    private val PREF_GUIDE_TEST = "pref_guide_test"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.setContentView(this)

        with(binding.rv) {
            adapter = MyAdapter()
//            binding.scrollbar.attachTo(this, object : MyScrollbarCallback {
//                override fun getTotalItemAmount(): Int = ITEM_COUNT
//
//                override fun onCurrentItemCountGet(currentCount: Int) {
//                    scrollbar.setIndicatorText("${currentCount.plus(1)} / $ITEM_COUNT")
//                }
//
//                override fun showBtnGoTop(isShow: Boolean) {
//
//                }
//
//            })
        }

        Handler(Looper.getMainLooper()).postDelayed(
            {
                val target1: View? = binding.rv.layoutManager?.getChildAt(2)
                val target2: View? = binding.rv.layoutManager?.getChildAt(4)
                showBubble(1, target1) {
                    showBubble(2, target2)
                }
            }, 500)


    }

    private fun showBubble(position: Int, target: View?, onSkip: () -> Unit = {}) {
        GuidelineView.buildAndShow(
            activity = this,
            key = PREF_GUIDE_TEST,
            debugMode = true,
            onSkip = onSkip,
        ) {
            targetView(target)
            title("Demo Title $position")
            subTitle("Demo SubTitle $position")
            positionGravity(Gravity.BOTTOM)
            alignGravity(Gravity.LEFT)
            triangleOffset(12)
        }
    }

    companion object {
        const val ITEM_COUNT = 500
    }
}