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
import com.alok.dailynews.R
import com.alok.dailynews.database.NewsDatabase
import com.alok.dailynews.databinding.FragmentNewsBinding
import com.alok.dailynews.interfaces.onSwipeRight
import com.alok.dailynews.models.LikedNewsItem
import com.alok.dailynews.models.NewsItem
import com.alok.dailynews.ui.SharedViewModelFactory
import com.alok.dailynews.ui.SharedViewModel
import com.alok.dailynews.utility.Utils
import com.mindorks.placeholderview.SwipeDecor
import com.mindorks.placeholderview.SwipePlaceHolderView
import com.mindorks.placeholderview.SwipeViewBuilder

class NewsFragment : Fragment(), onSwipeRight {

    private lateinit var fragmentNewsBinding: FragmentNewsBinding
    lateinit var sharedViewModel: SharedViewModel
    var newsItemList: ArrayList<NewsItem>? = null
    lateinit var swipeView: SwipePlaceHolderView
    val TAG: String = "NewsFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
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
        sharedViewModel = ViewModelProviders.of(requireActivity(), newsViewModelFactory).get(
            SharedViewModel::class.java)
        newsItemList = ArrayList()

        val bottomMargin = Utils.dpToPx(160)
        val windowSize : Point = Utils.getDisplaySize(requireActivity().windowManager)

        swipeView = fragmentNewsBinding!!.swipeView
        swipeView.getBuilder<SwipePlaceHolderView, SwipeViewBuilder<SwipePlaceHolderView>>()
            .setDisplayViewCount(3)
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

        return fragmentNewsBinding.root
    }

    private fun setNewsData(){
        sharedViewModel.newsItemList.observe(viewLifecycleOwner, Observer { tempNewsItem: ArrayList<NewsItem>? ->
            if (tempNewsItem != null) {
                for (newsItem: NewsItem in tempNewsItem)
                    swipeView.addView(NewsCard(requireContext(), newsItem, swipeView, this))
            }
        })
    }

    override fun onSwipeRight(likedNewsItem: LikedNewsItem) {
        sharedViewModel.insertLikedNewsItem(likedNewsItem)
    }

}