package com.alok.dailynews.utility

import android.content.Context
import android.content.SharedPreferences
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

    fun setHasUserGaveAppReview(context: Context, isUserReviewed: Boolean){
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean("HAS_USER_REVIEWED", isUserReviewed)
        editor.apply()
    }

    fun hasUserReviewedOurApp(context: Context): Boolean {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("HAS_USER_REVIEWED", false)
    }

    fun setLastAppReviewRequested(context: Context, timestamp: Long){
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putLong("LAST_APP_REVIEW_REQUESTED", timestamp)
        editor.apply()
    }

    fun getLastAppReviewRequestedTimestamp(context: Context): Long{
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        return sharedPreferences.getLong("LAST_APP_REVIEW_REQUESTED", 0)
    }
}