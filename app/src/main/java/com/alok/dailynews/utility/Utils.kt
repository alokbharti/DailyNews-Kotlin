package com.alok.dailynews.utility

import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager

object Utils {
    fun getDisplaySize(windowManager: WindowManager): Point {
        return try {
            if (Build.VERSION.SDK_INT > 16) {
                val display = windowManager.defaultDisplay
                val displayMetrics = DisplayMetrics()
                display.getMetrics(displayMetrics)
                Point(displayMetrics.widthPixels, displayMetrics.heightPixels)
            } else {
                Point(0, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Point(0, 0)
        }
    }

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
}