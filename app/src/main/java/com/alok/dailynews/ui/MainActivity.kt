package com.alok.dailynews.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.work.*
import com.alok.dailynews.R
import com.alok.dailynews.database.NewsDatabase
import com.alok.dailynews.databinding.ActivityMainBinding
import com.alok.dailynews.models.NewsItem
import com.alok.dailynews.utility.Constants.Companion.categorySelected
import com.alok.dailynews.utility.PeriodicBackgroundNotification
import com.alok.dailynews.utility.Utils
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.testing.FakeReviewManager
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var sharedViewModel: SharedViewModel
    lateinit var activityMainBinding: ActivityMainBinding
    val queryData = MutableLiveData<String>()
    private lateinit var pref: SharedPreferences
    private lateinit var periodWorkRequest: WorkRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        //delegate.localNightMode = MODE_NIGHT_YES
        val sharedPreferences = getSharedPreferences("com.alok.app",Context.MODE_PRIVATE)
        val isNightModeOn = sharedPreferences.getBoolean("NIGHT_MODE", false)
        when{
            isNightModeOn ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = activityMainBinding.root
        setContentView(view)
        setUpNavigation()

        sharedViewModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)

        handleIntent(intent)

        pref = getPreferences(Context.MODE_PRIVATE)
        //for showing notification
        periodWorkRequest = PeriodicWorkRequestBuilder<PeriodicBackgroundNotification>(
            6, TimeUnit.HOURS)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS)
            .addTag("periodic-pending-notification")
            .build()
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

        val isNotificationScheduled = pref.getBoolean("IS_NOTIFICATION_SCHEDULED", false)
        if (!isNotificationScheduled) {
            WorkManager.getInstance(this).enqueue(periodWorkRequest)
            val editor = pref.edit()
            editor.putBoolean("IS_NOTIFICATION_SCHEDULED", true)
            editor.apply()
        }
    }

}
