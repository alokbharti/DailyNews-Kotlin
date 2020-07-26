package com.alok.dailynews.ui.discover

import android.app.SearchManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alok.dailynews.BuildConfig
import com.alok.dailynews.R
import com.alok.dailynews.adapter.NewsCategoryAdapter
import com.alok.dailynews.adapter.SearchNewsAdapter
import com.alok.dailynews.databinding.FragmentDiscoverBinding
import com.alok.dailynews.interfaces.OnCustomClickListener
import com.alok.dailynews.models.NewsCategoryItem
import com.alok.dailynews.models.NewsItem
import com.alok.dailynews.ui.MainActivity
import com.alok.dailynews.utility.Constants
import com.alok.dailynews.utility.Constants.Companion.categorySelected
import com.alok.dailynews.utility.MySuggestionProvider

class DiscoverFragment : Fragment(), OnCustomClickListener<NewsCategoryItem>, SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private lateinit var binding: FragmentDiscoverBinding
    private lateinit var searchNewsAdapter: SearchNewsAdapter
    private lateinit var discoverViewModel: DiscoverViewModel
    private var newsList: ArrayList<NewsItem> = ArrayList()
    private val TAG = "DiscoverFragment"
    private val searchQuery = MutableLiveData<String>()
    lateinit var mainActivity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentDiscoverBinding.inflate(layoutInflater, container, false)
        discoverViewModel = ViewModelProviders.of(this).get(DiscoverViewModel::class.java)
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

        val searchManager : SearchManager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        binding.searchView.setOnQueryTextListener(this)
        binding.searchView.setOnCloseListener(this)
        binding.searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
        binding.searchView.isQueryRefinementEnabled = true

        mainActivity = activity as MainActivity
        mainActivity.queryData.observe(viewLifecycleOwner, Observer { query ->
            searchQuery.value = query
        })

        searchNewsAdapter = SearchNewsAdapter(newsList,
            object : OnCustomClickListener<NewsItem> {
                override fun onClick(obj: NewsItem) {
                    newsList.remove(obj)
                    newsList.add(0, obj)
                    mainActivity.setNewsData(newsList)

                    val navController = Navigation.findNavController(binding.root)
                    navController.navigate(DiscoverFragmentDirections.actionDiscoverFragmentToNewsFragment(
                        categorySelected))
                }
            })
        binding.searchNewsRv.layoutManager = LinearLayoutManager(context)
        binding.searchNewsRv.setHasFixedSize(false)
        binding.searchNewsRv.adapter = searchNewsAdapter

        searchQuery.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                binding.searchView.setQuery(it, false)
                binding.searchView.clearFocus()
                binding.searchNewsRv.visibility = View.VISIBLE
                setNewsData(it)
            }
        })

        val closeBtnId = binding.searchView.context.resources.getIdentifier("android:id/search_close_btn", null, null)
        var closeButton: ImageView = binding.searchView.findViewById(closeBtnId)
        closeButton.setOnClickListener{
            binding.searchNewsRv.isVisible = false
            binding.searchView.setQuery("", false)
        }

        val sharedPreferences = requireActivity().getSharedPreferences("com.alok.app",Context.MODE_PRIVATE)
        val isNightModeOn = sharedPreferences.getBoolean("NIGHT_MODE", false)
        when{
            isNightModeOn -> binding.modeToggleBtn.isChecked = true
            else -> binding.modeToggleBtn.isChecked = false
        }

        binding.modeToggleBtn.setOnClickListener{
            sharedPreferences.edit().putBoolean("NIGHT_MODE", !isNightModeOn).apply()
            requireActivity().recreate()
        }
    }

    override fun onClick(obj: NewsCategoryItem) {
        val navController = Navigation.findNavController(binding.root)
        navController.navigate(DiscoverFragmentDirections.actionDiscoverFragmentToNewsFragment(obj.newsCategoryName))
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        searchQuery.value = query
        val suggestions = SearchRecentSuggestions(context, MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE)
        suggestions.saveRecentQuery(query, null)

        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    override fun onClose(): Boolean {
        binding.searchNewsRv.visibility = View.GONE
        return true
    }

    fun setNewsData(query: String){
        val imageUrl = "https://newsapi.org/v2/everything?q=$query&sortBy=publishedAt&language=en&apiKey="+
                /*requireContext().resources.getString(R.string.news_api_key)*/BuildConfig.API_KEY
        discoverViewModel.getSearchedNewsData(imageUrl).observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "newsSearched list size: "+it.size)
            newsList.clear()
            newsList.addAll(it)
            searchNewsAdapter.notifyDataSetChanged()
        })
    }

    override fun onStop() {
        super.onStop()
        mainActivity.setQueryToEmpty()
    }
}
