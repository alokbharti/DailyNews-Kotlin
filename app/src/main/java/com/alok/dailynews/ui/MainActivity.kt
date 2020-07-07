package com.alok.dailynews.ui

import android.app.SearchManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.alok.dailynews.R
import com.alok.dailynews.database.NewsDatabase
import com.alok.dailynews.databinding.ActivityMainBinding
import com.alok.dailynews.models.NewsItem
import com.alok.dailynews.utility.Constants.Companion.categorySelected

class MainActivity : AppCompatActivity() {

    private lateinit var sharedViewModel: SharedViewModel
    lateinit var activityMainBinding: ActivityMainBinding
    val queryData = MutableLiveData<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        //delegate.localNightMode = MODE_NIGHT_YES

        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = activityMainBinding.root
        setContentView(view)
        setUpNavigation()
        val application = requireNotNull(this).application
        val datasource = NewsDatabase.getInstance(application).newsDatabaseDao
        val newsViewModelFactory =
            SharedViewModelFactory(
                datasource,
                application
            )
        sharedViewModel = ViewModelProviders.of(this, newsViewModelFactory).get(SharedViewModel::class.java)

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                queryData.value = query
            }
        }
    }

    fun setQueryToEmpty(){
        queryData.value = ""
    }

    fun setNewsData(newsItemList : ArrayList<NewsItem>){
        sharedViewModel.updateNewsItemListFromSearch(newsItemList)
    }

    fun setUpNavigation(){
        val navHostFragment: NavHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        NavigationUI.setupWithNavController(activityMainBinding.bttmNav, navHostFragment.navController)
    }

    override fun onStop() {
        super.onStop()
        categorySelected = "General"
    }

}
