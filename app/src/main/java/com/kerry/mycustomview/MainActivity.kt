package com.kerry.mycustomview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.kerry.guidelinebubble.GuidelineView

class MainActivity : AppCompatActivity() {

    private val recyclerView: RecyclerView by lazy { findViewById(R.id.rv) }

    private val PREF_GUIDE_TEST = "pref_guide_test"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        with(recyclerView) {
            adapter = MyAdapter()
        }

        Handler(Looper.getMainLooper()).postDelayed(
            {
                val target1: View? = recyclerView.layoutManager?.getChildAt(6)
                val target2: View? = recyclerView.layoutManager?.getChildAt(8)
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
}