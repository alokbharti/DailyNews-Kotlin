package com.alok.dailynews

import android.util.Log
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alok.dailynews.database.NewsDatabase
import com.alok.dailynews.database.NewsDatabaseDao
import com.alok.dailynews.models.LikedNewsItem
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import java.io.IOException

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class NewsDatabaseTest {

    private lateinit var newsDatabaseDao: NewsDatabaseDao
    private lateinit var db: NewsDatabase

    @Before
    fun createDb(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, NewsDatabase::class.java)
                // Allowing main thread queries, just for testing.
                .allowMainThreadQueries()
                .build()

        newsDatabaseDao = db.newsDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb(){
        db.close()
    }
    @Test
    @Throws(Exception::class)
    fun insertAndGetNews() {
        val likedNewsItem = LikedNewsItem()
        newsDatabaseDao.insert(likedNewsItem)

        val newsArticle = newsDatabaseDao.getFirstNewsArticle(1)
        assertEquals(newsArticle?.title, "Demo Title")

    }
}
