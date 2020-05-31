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
        for (name in newsCategoryNameList){
            val newsCategoryItem = NewsCategoryItem(name, false)
            newsCategoryList.add(newsCategoryItem)
        }

        val adapter = NewsCategoryAdapter(newsCategoryList, this)
        binding.topicsRv.layoutManager = GridLayoutManager(context, 3)
        binding.topicsRv.setHasFixedSize(false)
        binding.topicsRv.adapter = adapter
    }

    override fun onClick(obj: NewsCategoryItem) {
        Toast.makeText(context, obj.newsCategoryName, Toast.LENGTH_SHORT).show();
        val bundle = Bundle()
        bundle.putString("category_selected", obj.newsCategoryName)
        val navController = Navigation.findNavController(binding.root)
        navController.navigate(DiscoverFragmentDirections.actionDiscoverFragmentToNewsFragment(obj.newsCategoryName))
    }

}
