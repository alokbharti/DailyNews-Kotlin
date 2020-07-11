package com.alok.dailygraduation.ui.graduation

import android.content.Context
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.alok.dailynews.R
import com.alok.dailynews.interfaces.OnSwipe
import com.alok.dailynews.models.GraduationItem
import com.bumptech.glide.Glide
import com.mindorks.placeholderview.annotations.Layout
import com.mindorks.placeholderview.annotations.Resolve
import com.mindorks.placeholderview.annotations.View
import com.mindorks.placeholderview.annotations.swipe.*

@Layout(R.layout.item_graduation)
class GraduationCard(private val context: Context, private val graduationItem: GraduationItem,
                     private val swipe: OnSwipe<GraduationItem>) {
    
    @View(R.id.graduation_image_iv)
    lateinit var graduationImageView: ImageView
    @View(R.id.graduation_title_tv)
    lateinit var graduationTitle: TextView

    @Resolve
    fun onResolved(){
        Glide.with(context).load(graduationItem.imageUrl).placeholder(R.drawable.graduation_cap).into(graduationImageView)
        graduationTitle.text = graduationItem.title
    }

    @SwipeOut
    fun onSwipeOut(){
        Log.d("EVENT", "onSwipedOut")
        swipe.onSwipeLeft(graduationItem)
    }


    @SwipeCancelState
    fun onSwipeCancelState() {
        Log.d("EVENT", "onSwipeCancelState")
    }

    @SwipeIn
    fun onSwipeIn() {
        Log.d("EVENT", "onSwipedIn")
        swipe.onSwipeRight(graduationItem)
    }

    @SwipeInState
    fun onSwipeInState() {
    }

    @SwipeOutState
    fun onSwipeOutState() {
    }
    
}