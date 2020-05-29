package com.alok.dailynews.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alok.dailynews.R
import com.alok.dailynews.databinding.ItemNewsBinding
import com.alok.dailynews.models.NewsItem
import com.bumptech.glide.Glide

class NewsAdapter(var newsArticleList: ArrayList<NewsItem>?, var context: Context?) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemNewsBinding: ItemNewsBinding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemNewsBinding)
    }

    override fun getItemCount(): Int {
        return newsArticleList?.size ?:0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (newsArticleList!=null) {
            val newsItem: NewsItem = newsArticleList!![position]
            holder.newsTitle.text = newsItem.title
            holder.newsDesc.text = newsItem.description
            Glide.with(this.context!!).load(newsItem.imageUrl).placeholder(R.drawable.dailynews).into(holder.newsImage)

            holder.newsFrameLayout.layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            holder.newsFrameLayout.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(newsItem.newsUrl))
                context!!.startActivity(browserIntent)
            }
        }
    }

    class ViewHolder(itemNewsBinding: ItemNewsBinding) : RecyclerView.ViewHolder(itemNewsBinding.root){
        val newsTitle: TextView = itemNewsBinding.newsTitleTv
        val newsDesc: TextView = itemNewsBinding.newsDescTv
        val newsImage: ImageView = itemNewsBinding.newsImageIv
        val newsFrameLayout = itemNewsBinding.newsItemFl
    }
}