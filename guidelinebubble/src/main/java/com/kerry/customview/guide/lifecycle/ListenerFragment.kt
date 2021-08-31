package com.kerry.customview.guide.lifecycle


import androidx.fragment.app.Fragment

class ListenerFragment : Fragment() {

    private lateinit var mFragmentLifecycle: FragmentLifecycle

    fun setFragmentLifecycle(lifecycle: FragmentLifecycle) {
        mFragmentLifecycle = lifecycle
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::mFragmentLifecycle.isInitialized) {
            mFragmentLifecycle.onDestroyView()
        }
    }
}