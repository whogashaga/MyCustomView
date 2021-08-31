package com.kerry.customview.guide.lifecycle

interface FragmentLifecycle {
    fun onStart()

    fun onStop()

    fun onDestroyView()

    fun onDestroy()
}