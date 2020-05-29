package com.alok.dailynews.ui.liked

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager

import com.alok.dailynews.adapter.NewsAdapter
import com.alok.dailynews.database.NewsDatabase
import com.alok.dailynews.databinding.FragmentLikedNewsBinding
import com.alok.dailynews.models.LikedNewsItem
import com.alok.dailynews.models.NewsItem
import com.alok.dailynews.ui.SharedViewModelFactory
import com.alok.dailynews.ui.SharedViewModel

class LikedNewsFragment : Fragment() {

    private lateinit var likedNewsViewModel: SharedViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding: FragmentLikedNewsBinding = FragmentLikedNewsBinding.inflate(layoutInflater, container, false)

        val application = requireNotNull(this.activity).application
        val datasource = NewsDatabase.getInstance(application).newsDatabaseDao
        val newsViewModelFactory = SharedViewModelFactory(datasource, application)
        likedNewsViewModel = ViewModelProviders.of(requireActivity(), newsViewModelFactory).get(SharedViewModel::class.java)

        var newsItemList: ArrayList<NewsItem> = ArrayList()
        var adapter = NewsAdapter(newsItemList, context)
        binding.newsRv.layoutManager = LinearLayoutManager(context)
        binding.newsRv.setHasFixedSize(false)
        binding.newsRv.adapter = adapter

        likedNewsViewModel.likedNewsItems.observe(viewLifecycleOwner, Observer {tempLikedNewsItems: List<LikedNewsItem> ->
            Log.d("LikedNewsFragment", "in likedNewsItems observer")
            if (tempLikedNewsItems.isNotEmpty()) {
                binding.noItemsFoundTv.visibility = View.GONE
                newsItemList.clear()
                for (likedNewsItem in tempLikedNewsItems){
                    val newsItem = NewsItem(likedNewsItem.title, likedNewsItem.description,
                        likedNewsItem.imageUrl, likedNewsItem.newsUrl, true)
                    newsItemList.add(newsItem)
                    adapter.notifyDataSetChanged()
                }
            } else {
                binding.noItemsFoundTv.visibility = View.VISIBLE
                //Toast.makeText(context, "No data added", Toast.LENGTH_SHORT).show()
            }
        })

        return binding.root
    }

}
