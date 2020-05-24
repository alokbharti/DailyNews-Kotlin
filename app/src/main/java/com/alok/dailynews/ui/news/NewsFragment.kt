package com.alok.dailynews.ui.news

import android.graphics.Point
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alok.dailynews.R
import com.alok.dailynews.databinding.FragmentNewsBinding
import com.alok.dailynews.models.NewsItem
import com.alok.dailynews.utility.Utils
import com.mindorks.placeholderview.SwipeDecor
import com.mindorks.placeholderview.SwipePlaceHolderView
import com.mindorks.placeholderview.SwipeViewBuilder

class NewsFragment : Fragment() {

    lateinit var fragmentNewsBinding: FragmentNewsBinding
    lateinit var newsViewModel: NewsViewModel
    var newsItemList: ArrayList<NewsItem>? = null
    lateinit var swipeView: SwipePlaceHolderView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        fragmentNewsBinding = FragmentNewsBinding.inflate(layoutInflater)
        val fragmentView = fragmentNewsBinding.root

        newsViewModel = ViewModelProviders.of(this).get(NewsViewModel::class.java)
        val imageUrl = "https://newsapi.org/v2/top-headlines?language=en&page=1&apiKey=2012066be1c944409c701878d544b5fc"
        newsItemList = ArrayList()

        val bottomMargin = Utils.dpToPx(160)
        val windowSize : Point = Utils.getDisplaySize(requireActivity().windowManager)

        swipeView = fragmentNewsBinding.swipeView
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

        setNewsData(imageUrl)

        fragmentNewsBinding.bookmarkBtn.setOnClickListener {
            swipeView.doSwipe(true)
        }

        fragmentNewsBinding.cancelBtn.setOnClickListener {
            swipeView.doSwipe(false)
        }

        return fragmentView
    }

    fun setNewsData(url:String){
        newsViewModel.getNewsItemList(url).observe(viewLifecycleOwner, Observer { tempNewsItem: ArrayList<NewsItem>? ->
            if (tempNewsItem != null) {
                for (newsItem: NewsItem in tempNewsItem)
                    swipeView.addView(NewsCard(requireContext(), newsItem, swipeView))
            }
        })
    }

}