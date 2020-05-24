package com.alok.dailynews.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.viewpager.widget.PagerAdapter
import com.alok.dailynews.R
import com.alok.dailynews.models.NewsItem
import com.bumptech.glide.Glide

class ScreenSlidePagerAdapter(var context: Context, var newsItemList: ArrayList<NewsItem>?) :PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return newsItemList?.size?:0
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView: View = LayoutInflater.from(context).inflate(R.layout.item_news, container, false)
        val newsTitle: TextView = itemView.findViewById(R.id.news_title_tv)
        val newsDesc: TextView = itemView.findViewById(R.id.news_desc_tv)
        val newsImage: ImageView = itemView.findViewById(R.id.news_image_iv)

        if (newsItemList!=null) {
            val newsItem = newsItemList!!.get(position)
            newsTitle.text = newsItem.title
            newsDesc.text = newsItem.description
            try {
                Glide.with(context).load(newsItem.imageUrl).into(newsImage)
            } catch (exception: Exception) {
                Glide.with(context).load(R.drawable.dailynews).into(newsImage)
            }
        }

        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as CardView)
    }
}