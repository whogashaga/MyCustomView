package com.kerry.guidelinebubble.guide.lifecycle

interface FragmentLifecycle {
    fun onStart()

    fun onStop()

    fun onDestroyView()

    fun onDestroy()
}