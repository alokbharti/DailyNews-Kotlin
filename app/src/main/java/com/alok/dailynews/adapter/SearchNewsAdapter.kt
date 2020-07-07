package com.alok.dailynews.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alok.dailynews.R
import com.alok.dailynews.databinding.ItemSearchedNewsBinding
import com.alok.dailynews.interfaces.OnCustomClickListener
import com.alok.dailynews.models.NewsItem
import com.bumptech.glide.Glide

class SearchNewsAdapter(var newsList : ArrayList<NewsItem>,
                        var onCustomClickListener: OnCustomClickListener<NewsItem>)
    : RecyclerView.Adapter<SearchNewsAdapter.ViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val itemSearchedNewsBinding: ItemSearchedNewsBinding = ItemSearchedNewsBinding.inflate(
            LayoutInflater.from(context), parent, false)
        return ViewHolder(itemSearchedNewsBinding)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val newsItem = newsList[position]
        holder.newsTitleTv.text = newsItem.title
        Glide.with(context).load(newsItem.imageUrl).placeholder(R.drawable.dailynews).into(holder.newsImage)
        holder.newsSearchedLL.setOnClickListener(){
            onCustomClickListener.onClick(newsItem)
        }
    }

    class ViewHolder(binding: ItemSearchedNewsBinding): RecyclerView.ViewHolder(binding.root){
        val newsImage: ImageView = binding.newsSearchedImageIv
        val newsTitleTv: TextView = binding.newsSearchedTitleTv
        val newsSearchedLL: LinearLayout = binding.newsSaerchedLl
    }

}