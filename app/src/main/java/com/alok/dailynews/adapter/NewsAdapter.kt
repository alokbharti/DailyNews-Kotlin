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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alok.dailynews.R
import com.alok.dailynews.databinding.ItemNewsBinding
import com.alok.dailynews.interfaces.OnCustomClickListener
import com.alok.dailynews.models.LikedNewsItem
import com.bumptech.glide.Glide

class NewsAdapter(var context: Context?,
                  var onCustomClickListener: OnCustomClickListener<LikedNewsItem>)
    : ListAdapter<LikedNewsItem, NewsAdapter.ViewHolder>(LikedNewsItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemNewsBinding: ItemNewsBinding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemNewsBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val newsItem: LikedNewsItem = getItem(position)
        holder.newsTitle.text = newsItem.title
        holder.newsDesc.text = newsItem.description
        Glide.with(this.context!!).load(newsItem.imageUrl).placeholder(R.drawable.dailynews)
            .into(holder.newsImage)
        holder.newsSourceName.text = String.format("click on the card for more at %s", newsItem.newsSourceName)

        holder.newsFrameLayout.layoutParams =
            FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        holder.newsFrameLayout.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(newsItem.newsUrl))
            context!!.startActivity(browserIntent)
        }
        holder.likeBtn.visibility = View.VISIBLE
        holder.likeBtn.setOnClickListener {
            newsItem.isBookmarked = 0
            onCustomClickListener.onClick(newsItem)
        }
    }

    class ViewHolder(itemNewsBinding: ItemNewsBinding) : RecyclerView.ViewHolder(itemNewsBinding.root){
        val newsTitle: TextView = itemNewsBinding.newsTitleTv
        val newsDesc: TextView = itemNewsBinding.newsDescTv
        val newsImage: ImageView = itemNewsBinding.newsImageIv
        val newsSourceName: TextView = itemNewsBinding.sourceTv
        val newsFrameLayout = itemNewsBinding.newsItemFl
        val likeBtn = itemNewsBinding.likeIb
    }

    class LikedNewsItemDiffCallback : DiffUtil.ItemCallback<LikedNewsItem>(){
        override fun areItemsTheSame(oldItem: LikedNewsItem, newItem: LikedNewsItem): Boolean {
            return oldItem.newsId == newItem.newsId
        }

        override fun areContentsTheSame(oldItem: LikedNewsItem, newItem: LikedNewsItem): Boolean {
            return oldItem == newItem
        }

    }
}