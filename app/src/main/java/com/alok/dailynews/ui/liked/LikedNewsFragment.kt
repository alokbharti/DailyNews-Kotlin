package com.alok.dailynews.ui.liked

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager

import com.alok.dailynews.adapter.NewsAdapter
import com.alok.dailynews.databinding.FragmentLikedNewsBinding
import com.alok.dailynews.interfaces.OnCustomClickListener
import com.alok.dailynews.models.LikedNewsItem
import com.alok.dailynews.ui.MainActivity
import com.alok.dailynews.ui.SharedViewModel
import com.alok.dailynews.utility.Utils
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.testing.FakeReviewManager

class LikedNewsFragment : Fragment(), OnCustomClickListener<LikedNewsItem> {

    private lateinit var likedNewsViewModel: SharedViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding: FragmentLikedNewsBinding = FragmentLikedNewsBinding.inflate(layoutInflater, container, false)

        likedNewsViewModel = ViewModelProviders.of(requireActivity()).get(SharedViewModel::class.java)

        val adapter = NewsAdapter(context, this)
        binding.newsRv.layoutManager = LinearLayoutManager(context)
        binding.newsRv.setHasFixedSize(false)
        binding.newsRv.adapter = adapter

        likedNewsViewModel.likedNewsItems.observe(viewLifecycleOwner, Observer {tempLikedNewsItems: List<LikedNewsItem>? ->
            Log.d("LikedNewsFragment", "in likedNewsItems observer")
            if (tempLikedNewsItems != null && tempLikedNewsItems.isNotEmpty()) {
                Log.d("LikedNewsFragment", "list size ${tempLikedNewsItems.size}")
                binding.noItemsFoundTv.visibility = View.GONE
            } else {
                binding.noItemsFoundTv.visibility = View.VISIBLE
            }
            adapter.submitList(tempLikedNewsItems)
        })

        //check the number of liked news item. If it is more than 10, then it means that
        //user has interacted enough to give a review for the app but also check the last app
        //review requested timestamp. Interval should be of minimum 1 month
        //first check whether has reviewed our app or not
        var isTimeConstraintGood = false
        val lastAppReviewRequestTimestamp = likedNewsViewModel.getLastAppReviewRequestedTimestamp()
        Log.d("LikedNewsFragment", "last timestamp: $lastAppReviewRequestTimestamp")
        if(lastAppReviewRequestTimestamp > 0){
            val diff = System.currentTimeMillis() - lastAppReviewRequestTimestamp
            val oneMonthTimestamp = 2592000000 //in millis
            if (diff > oneMonthTimestamp){
                isTimeConstraintGood = true
            }
        } else {
            isTimeConstraintGood = true
        }

        if(!likedNewsViewModel.hasUserReviewedOurApp()) {
            likedNewsViewModel.numberOfLikedNewsItem.observe(viewLifecycleOwner, Observer {
                if (it != null && it > 10 && isTimeConstraintGood) {
                    likedNewsViewModel.setLastAppReviewRequestedTimestamp()
                    askForReview()
                }
            })
        }

        return binding.root
    }

    override fun onClick(obj: LikedNewsItem) {
        Log.d("LikedNewsFragment", "in onClick of delete")
        likedNewsViewModel.deleteLikedNewsItem(obj)
    }

    private fun askForReview() {
        val manager = ReviewManagerFactory.create(requireContext())
        var reviewInfo: ReviewInfo
        manager.requestReviewFlow().addOnCompleteListener{request ->
            if(request.isSuccessful){
                reviewInfo = request.result
                manager.launchReviewFlow(requireActivity(), reviewInfo).addOnCompleteListener{
                    Log.d("LikedNewsFragment", "success launch")
                    //success app review, go with the flow
                    likedNewsViewModel.setHasUserReviewedOurApp(true)
                }.addOnFailureListener{
                    //launching failed
                }
            }
        }
    }
}
