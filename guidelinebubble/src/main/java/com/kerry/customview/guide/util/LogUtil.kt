package com.kerry.customview.guide.util

import android.text.TextUtils
import android.util.Log
import com.kerry.customview.guide.KerryGuide
import java.util.*

object LogUtil {

    private val NONE = 8
    private val tagPrefix = KerryGuide.TAG


    /**
     * 修改log level
     */
    val level = NONE
    //    public static final int level = Log.VERBOSE;

    /**
     * 取得Tag
     */
    private fun generateTag(): String {
        val stackTraceElement = Thread.currentThread().stackTrace[4]
        var callerClazzName = stackTraceElement.className
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1)
        var tag = "%s.%s(L:%d)"
        tag = String.format(
            Locale.getDefault(),
            tag,
            callerClazzName,
            stackTraceElement.methodName,
            stackTraceElement.lineNumber
        )
        //给tag设置前缀
        tag = if (TextUtils.isEmpty(tagPrefix)) tag else "$tagPrefix:$tag"
        return tag
    }

    fun v(msg: String) {
        if (level <= Log.VERBOSE) {
            val tag = generateTag()
            Log.v(tag, msg)
        }
    }

    fun v(msg: String, tr: Throwable) {
        if (level <= Log.VERBOSE) {
            val tag = generateTag()
            Log.v(tag, msg, tr)
        }
    }

    fun d(msg: String) {
        if (level <= Log.DEBUG) {
            val tag = generateTag()
            Log.d(tag, msg)
        }
    }

    fun d(msg: String, tr: Throwable) {
        if (level <= Log.DEBUG) {
            val tag = generateTag()
            Log.d(tag, msg, tr)
        }
    }

    fun i(msg: String) {
        if (level <= Log.INFO) {
            val tag = generateTag()
            Log.i(tag, msg)
        }
    }

    fun i(msg: String, tr: Throwable) {
        if (level <= Log.INFO) {
            val tag = generateTag()
            Log.i(tag, msg, tr)
        }
    }

    fun w(msg: String) {
        if (level <= Log.WARN) {
            val tag = generateTag()
            Log.w(tag, msg)
        }
    }

    fun w(msg: String, tr: Throwable) {
        if (level <= Log.WARN) {
            val tag = generateTag()
            Log.w(tag, msg, tr)
        }
    }

    fun e(msg: String) {
        if (level <= Log.ERROR) {
            val tag = generateTag()
            Log.e(tag, msg)
        }
    }

    fun e(msg: String, tr: Throwable) {
        if (level <= Log.ERROR) {
            val tag = generateTag()
            Log.e(tag, msg, tr)
        }
    }
}  