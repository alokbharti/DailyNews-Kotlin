package com.alok.dailynews.ui.news

import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alok.dailynews.BuildConfig
import com.alok.dailynews.R
import com.alok.dailynews.database.NewsDatabase
import com.alok.dailynews.databinding.FragmentNewsBinding
import com.alok.dailynews.interfaces.OnSwipe
import com.alok.dailynews.models.NewsItem
import com.alok.dailynews.ui.SharedViewModelFactory
import com.alok.dailynews.ui.SharedViewModel
import com.alok.dailynews.utility.Constants.Companion.categorySelected
import com.alok.dailynews.utility.Utils
import com.mindorks.placeholderview.SwipeDecor
import com.mindorks.placeholderview.SwipePlaceHolderView
import com.mindorks.placeholderview.SwipeViewBuilder

class NewsFragment : Fragment(), OnSwipe<NewsItem> {

    private lateinit var fragmentNewsBinding: FragmentNewsBinding
    lateinit var sharedViewModel: SharedViewModel
    var newsItemList: ArrayList<NewsItem>? = null
    lateinit var swipeView: SwipePlaceHolderView
    val TAG: String = "NewsFragment"
    var page = 1
    var isCategoryChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        fragmentNewsBinding = FragmentNewsBinding.inflate(layoutInflater)

        val application = requireNotNull(this.activity).application
        val datasource = NewsDatabase.getInstance(application).newsDatabaseDao
        val newsViewModelFactory =
            SharedViewModelFactory(datasource)
        sharedViewModel = ViewModelProviders.of(requireActivity(), newsViewModelFactory).get(SharedViewModel::class.java)
        newsItemList = ArrayList()

        val bottomMargin = Utils.dpToPx(160)
        val windowSize : Point = Utils.getDisplaySize(requireActivity().windowManager)

        swipeView = fragmentNewsBinding.swipeView
        swipeView.getBuilder<SwipePlaceHolderView, SwipeViewBuilder<SwipePlaceHolderView>>()
            .setDisplayViewCount(3)
            .setWidthSwipeDistFactor(8f) // horizontal distance = display width / 8
            .setHeightSwipeDistFactor(10f) // vertical distance = display height / 10
            .setSwipeDecor(SwipeDecor()
                .setViewWidth(windowSize.x)
                .setViewHeight(windowSize.y - bottomMargin)
                .setViewGravity(Gravity.TOP)
                .setPaddingTop(20)
                .setRelativeScale(0.01f)
                .setSwipeInMsgLayoutId(R.layout.swipe_in_view)
                .setSwipeOutMsgLayoutId(R.layout.swipe_out_view))

        fragmentNewsBinding.bookmarkBtn.setOnClickListener {
            swipeView.doSwipe(true)
        }

        fragmentNewsBinding.cancelBtn.setOnClickListener {
            swipeView.doSwipe(false)
        }

        val category = arguments?.getString("category_selected")
        if (category!=null && !category.equals(categorySelected)){
            Log.d(TAG, "selected category: $category")
            isCategoryChanged = true
            categorySelected = category
            fragmentNewsBinding.loadingLl.visibility = View.VISIBLE
            val url = "https://newsapi.org/v2/top-headlines?country=in&category=$category&apiKey="+
                    BuildConfig.API_KEY
            sharedViewModel.getNewsItemList(imageUrl = url)
        }
        setNewsData()

        swipeView.addItemRemoveListener {
            Log.d(TAG, "item removed: $it")
            if (it == 5){
                page+=1
                isCategoryChanged = false
                val url = "https://newsapi.org/v2/top-headlines?country=in&category=$categorySelected&page=$page&apiKey="+
                        BuildConfig.API_KEY
                sharedViewModel.getNewsItemList(imageUrl = url)
            }
        }

        return fragmentNewsBinding.root
    }

    private fun setNewsData(){

        sharedViewModel.newsItemList.observe(viewLifecycleOwner, Observer { tempNewsItem: ArrayList<NewsItem>? ->
            //hide loading layout
            fragmentNewsBinding.loadingLl.visibility = View.GONE
            Log.d(TAG, "in setNewsData")
            Log.d(TAG, "isCategoryChanged: $isCategoryChanged")
            if (tempNewsItem != null) {
                if (isCategoryChanged) {
                    swipeView.removeAllViews()
                }
                for (newsItem: NewsItem in tempNewsItem)
                    swipeView.addView(NewsCard(requireContext(), newsItem, this))

                Log.d(TAG, "total view count: ${swipeView.allResolvers.size}")
            } else {
                fragmentNewsBinding.loadingLl.visibility = View.VISIBLE
                fragmentNewsBinding.loadingTv.visibility = View.GONE
                fragmentNewsBinding.noDataTv.visibility = View.VISIBLE
            }
        })

    }

    override fun onSwipeRight(item: NewsItem) {
        sharedViewModel.removeSwipedArticle(item)
        sharedViewModel.insertLikedNewsItem(item)
    }

    override fun onSwipeLeft(item: NewsItem) {
        sharedViewModel.removeSwipedArticle(item)
    }

}