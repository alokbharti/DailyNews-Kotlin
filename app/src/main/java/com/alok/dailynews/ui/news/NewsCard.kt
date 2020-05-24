package com.alok.dailynews.ui.news

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import com.alok.dailynews.R
import com.alok.dailynews.models.NewsItem
import com.bumptech.glide.Glide
import com.mindorks.placeholderview.SwipePlaceHolderView
import com.mindorks.placeholderview.annotations.Click
import com.mindorks.placeholderview.annotations.Layout
import com.mindorks.placeholderview.annotations.Resolve
import com.mindorks.placeholderview.annotations.View
import com.mindorks.placeholderview.annotations.swipe.*


@Layout(R.layout.item_news)
class NewsCard (private val context:Context, private val newsItem: NewsItem, private val swipePlaceHolderView: SwipePlaceHolderView) {

    @View(R.id.news_image_iv)
    lateinit var newsImageView: ImageView
    @View(R.id.news_title_tv)
    lateinit var newsTitle: TextView
    @View(R.id.news_desc_tv)
    lateinit var newsDesc: TextView

    @Resolve
    fun onResolved(){
        Glide.with(context).load(newsItem.imageUrl).placeholder(R.drawable.dailynews).into(newsImageView)
        newsTitle.text = newsItem.title
        newsDesc.text = newsItem.description
    }

    @Click(R.id.news_card)
    fun onClick(){
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(newsItem.newsUrl))
        context.startActivity(browserIntent)
    }

    @SwipeOut
    fun onSwipeOut(){
        Log.d("EVENT", "onSwipedOut");
        swipePlaceHolderView.addView(this)
    }


    @SwipeCancelState
    fun onSwipeCancelState() {
        Log.d("EVENT", "onSwipeCancelState")
    }

    @SwipeIn
    fun onSwipeIn() {
        Log.d("EVENT", "onSwipedIn")
    }

    @SwipeInState
    fun onSwipeInState() {
        Log.d("EVENT", "onSwipeInState")
    }

    @SwipeOutState
    fun onSwipeOutState() {
        Log.d("EVENT", "onSwipeOutState")
    }
}