package com.alok.dailynews.ui

import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.alok.dailynews.R
import com.alok.dailynews.databinding.ActivityMainBinding
import com.alok.dailynews.models.NewsItem
import com.alok.dailynews.ui.liked.LikedNewsFragment
import com.alok.dailynews.ui.news.NewsFragment
import com.alok.dailynews.utility.Utils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mindorks.placeholderview.SwipeDecor
import com.mindorks.placeholderview.SwipePlaceHolderView
import com.mindorks.placeholderview.SwipeViewBuilder

class MainActivity : AppCompatActivity() {

    lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = activityMainBinding.root
        setContentView(view)
        setUpNavigation()
    }


    fun setUpNavigation(){
        val navHostFragment: NavHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        NavigationUI.setupWithNavController(activityMainBinding.bttmNav, navHostFragment.navController)
    }

}
