package com.alok.dailynews.ui.news

import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alok.dailynews.R
import com.alok.dailynews.database.NewsDatabase
import com.alok.dailynews.databinding.FragmentNewsBinding
import com.alok.dailynews.interfaces.OnSwipe
import com.alok.dailynews.models.NewsItem
import com.alok.dailynews.ui.SharedViewModelFactory
import com.alok.dailynews.ui.SharedViewModel
import com.alok.dailynews.utility.Constants.Companion.categorySelected
import com.alok.dailynews.utility.Constants.Companion.swipedOutCount
import com.alok.dailynews.utility.Utils
import com.mindorks.placeholderview.SwipeDecor
import com.mindorks.placeholderview.SwipePlaceHolderView
import com.mindorks.placeholderview.SwipeViewBuilder

class NewsFragment : Fragment(), OnSwipe {

    private lateinit var fragmentNewsBinding: FragmentNewsBinding
    lateinit var sharedViewModel: SharedViewModel
    var newsItemList: ArrayList<NewsItem>? = null
    lateinit var swipeView: SwipePlaceHolderView
    val TAG: String = "NewsFragment"
    var totalNumberOfNewsItem = 0
    var page = 1
    var totalSwipedOutCount = MutableLiveData<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        totalSwipedOutCount.value = swipedOutCount
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        fragmentNewsBinding = FragmentNewsBinding.inflate(layoutInflater)

        val application = requireNotNull(this.activity).application
        val datasource = NewsDatabase.getInstance(application).newsDatabaseDao
        val newsViewModelFactory =
            SharedViewModelFactory(
                datasource,
                application
            )
        sharedViewModel = ViewModelProviders.of(requireActivity(), newsViewModelFactory).get(SharedViewModel::class.java)
        newsItemList = ArrayList()

        val bottomMargin = Utils.dpToPx(160)
        val windowSize : Point = Utils.getDisplaySize(requireActivity().windowManager)

        swipeView = fragmentNewsBinding.swipeView
        swipeView.getBuilder<SwipePlaceHolderView, SwipeViewBuilder<SwipePlaceHolderView>>()
            .setDisplayViewCount(5)
            .setWidthSwipeDistFactor(8f) // horizontal distance = display width / 8
            .setHeightSwipeDistFactor(10f) // vertical distance = display height / 10
            .setSwipeDecor(
                SwipeDecor()
                    .setViewWidth(windowSize.x)
                    .setViewHeight(windowSize.y - bottomMargin)
                    .setViewGravity(Gravity.TOP)
                    .setPaddingTop(20)
                    .setRelativeScale(0.01f)
                    .setSwipeInMsgLayoutId(R.layout.swipe_in_view)
                    .setSwipeOutMsgLayoutId(R.layout.swipe_out_view))

        setNewsData()

        fragmentNewsBinding.bookmarkBtn.setOnClickListener {
            swipeView.doSwipe(true)
        }

        fragmentNewsBinding.cancelBtn.setOnClickListener {
            swipeView.doSwipe(false)
        }

        val category = arguments?.getString("category_selected")
        if (category!=null && !category.equals(categorySelected)){
            Log.d(TAG, "selected category: $category")
            categorySelected = category
            fragmentNewsBinding.loadingLl.visibility = View.VISIBLE
            val url = "https://newsapi.org/v2/top-headlines?country=in&category=$category&apiKey="+
                    application.baseContext.resources.getString(R.string.news_api_key)
            sharedViewModel.getNewsItemList(imageUrl = url)
        }

        totalSwipedOutCount.observe(viewLifecycleOwner, Observer { swipeOutCount: Int ->

            Log.d(TAG, "Total News Item Count: $totalNumberOfNewsItem, swipedOutCount: $swipeOutCount")
            if (totalNumberOfNewsItem!=0 && (totalNumberOfNewsItem - swipeOutCount <5)){
                page+=1
                val url = "https://newsapi.org/v2/top-headlines?country=in&category=$categorySelected&page=$page&apiKey="+
                        application.baseContext.resources.getString(R.string.news_api_key)
                sharedViewModel.getNewsItemList(imageUrl = url)
                swipedOutCount  = 0
                totalSwipedOutCount.value = swipedOutCount
            }
        })

        return fragmentNewsBinding.root
    }

    private fun setNewsData(){

        swipedOutCount  = 0
        totalSwipedOutCount.value = swipedOutCount

        sharedViewModel.newsItemList.observe(viewLifecycleOwner, Observer { tempNewsItem: ArrayList<NewsItem>? ->
            //hide loading layout
            fragmentNewsBinding.loadingLl.visibility = View.GONE
            //swipeView.removeAllViews()
            Log.d(TAG, "in setNewsData")
            if (tempNewsItem != null) {
                totalNumberOfNewsItem = tempNewsItem.size
                Log.d(TAG, "newsItemList size: "+totalNumberOfNewsItem)
                for (newsItem: NewsItem in tempNewsItem)
                    swipeView.addView(NewsCard(requireContext(), newsItem, swipeView, this))
            } else {
                fragmentNewsBinding.loadingLl.visibility = View.VISIBLE
                fragmentNewsBinding.loadingTv.visibility = View.VISIBLE
                fragmentNewsBinding.noDataTv.visibility = View.VISIBLE
            }
        })

    }

    override fun onSwipeRight(newsItem: NewsItem) {
        sharedViewModel.removeSwipedArticle(newsItem)

        sharedViewModel.insertLikedNewsItem(newsItem)
        swipedOutCount +=1
        totalSwipedOutCount.value = swipedOutCount
    }

    override fun onSwipeLeft(newsItem: NewsItem) {
        sharedViewModel.removeSwipedArticle(newsItem)
        swipedOutCount +=1
        totalSwipedOutCount.value = swipedOutCount
    }

}