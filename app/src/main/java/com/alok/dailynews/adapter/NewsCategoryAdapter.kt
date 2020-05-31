package com.alok.dailynews.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.alok.dailynews.databinding.ItemNewsCategoryBinding
import com.alok.dailynews.interfaces.OnCustomClickListener
import com.alok.dailynews.models.NewsCategoryItem

class NewsCategoryAdapter(var newsCategoryList : ArrayList<NewsCategoryItem>,
                          var onCustomClickListener: OnCustomClickListener<NewsCategoryItem>): RecyclerView.Adapter<NewsCategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding : ItemNewsCategoryBinding = ItemNewsCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return newsCategoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var newsCategoryItem = newsCategoryList[position]
        holder.categoryTv.text = newsCategoryItem.newsCategoryName
        if (newsCategoryItem.isSelected){
            holder.categorySlectedIv.visibility = View.VISIBLE
        } else {
            holder.categorySlectedIv.visibility = View.INVISIBLE
        }
        holder.categoryCv.setOnClickListener(){
            newsCategoryItem.isSelected = !newsCategoryItem.isSelected
            notifyItemChanged(position)
            onCustomClickListener.onClick(newsCategoryItem)
        }

    }

    class ViewHolder(binding: ItemNewsCategoryBinding): RecyclerView.ViewHolder(binding.root) {
        val categoryTv: TextView = binding.newsCategoryTv
        val categorySlectedIv: ImageView = binding.newsSelectedIv
        val categoryCv: CardView = binding.categoryCv
    }
}