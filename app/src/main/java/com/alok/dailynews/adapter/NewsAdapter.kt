package com.alok.dailynews.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
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
            val newsItem: NewsItem = newsArticleList!!.get(position)
            holder.newsTitle.text = newsItem.title
            holder.newsDesc.text = newsItem.description
            try {
                Glide.with(this.context!!).load(newsItem.imageUrl).into(holder.newsImage)
            } catch (exception : Exception){
                Glide.with(this.context!!).load(R.drawable.dailynews).into(holder.newsImage)
            }
        }
    }

    class ViewHolder(itemNewsBinding: ItemNewsBinding) : RecyclerView.ViewHolder(itemNewsBinding.root){
        val newsTitle: TextView = itemNewsBinding.newsTitleTv
        val newsDesc: TextView = itemNewsBinding.newsDescTv
        val newsImage: ImageView = itemNewsBinding.newsImageIv
    }
}