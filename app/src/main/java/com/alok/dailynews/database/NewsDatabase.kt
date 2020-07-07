package com.alok.dailynews.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.alok.dailynews.models.LikedNewsItem

@Database(entities = [LikedNewsItem::class], version = 2, exportSchema = false)
abstract class NewsDatabase : RoomDatabase(){
    abstract val newsDatabaseDao: NewsDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: NewsDatabase? = null
        fun getInstance(context: Context): NewsDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        NewsDatabase::class.java,
                        "sleep_history_database"
                    )
                         //TODO: update migration scheme, currently it'll erase all the old data and create a new one
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}