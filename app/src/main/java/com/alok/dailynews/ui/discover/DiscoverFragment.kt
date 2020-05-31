package com.alok.dailynews.ui.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.alok.dailynews.R
import com.alok.dailynews.adapter.NewsCategoryAdapter
import com.alok.dailynews.databinding.FragmentDiscoverBinding
import com.alok.dailynews.interfaces.OnCustomClickListener
import com.alok.dailynews.models.NewsCategoryItem
import com.alok.dailynews.utility.Constants

class DiscoverFragment : Fragment(), OnCustomClickListener<NewsCategoryItem> {

    private lateinit var binding: FragmentDiscoverBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentDiscoverBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val newsCategoryList = ArrayList<NewsCategoryItem>()
        //TODO: try to get the categories from APIs
        val newsCategoryNameList = listOf("General","Entertainment", "Health", "Sports", "Science", "Technology", "Business")
        val newsCategoryImage = listOf(R.drawable.general_icon, R.drawable.entertainment_icon,
            R.drawable.health_icon, R.drawable.sports_icon, R.drawable.science_icon,
            R.drawable.technology_icon, R.drawable.business_icon)
        var i =0
        for (name in newsCategoryNameList){
            var isSelected = false
            if (Constants.categorySelected.equals(name)){
                isSelected = true
            }
            val newsCategoryItem = NewsCategoryItem(name,newsCategoryImage[i], isSelected)
            newsCategoryList.add(newsCategoryItem)
            i+=1
        }

        val adapter = NewsCategoryAdapter(newsCategoryList, this)
        binding.topicsRv.layoutManager = GridLayoutManager(context, 3)
        binding.topicsRv.setHasFixedSize(false)
        binding.topicsRv.adapter = adapter
    }

    override fun onClick(obj: NewsCategoryItem) {
        val navController = Navigation.findNavController(binding.root)
        navController.navigate(DiscoverFragmentDirections.actionDiscoverFragmentToNewsFragment(obj.newsCategoryName))
    }

}
