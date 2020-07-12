package com.alok.dailynews.utility

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.alok.dailynews.BuildConfig
import com.alok.dailynews.R
import com.alok.dailynews.models.NewsItem
import com.alok.dailynews.ui.MainActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.ANRequest
import com.androidnetworking.common.ANResponse
import com.androidnetworking.common.Priority
import org.json.JSONArray
import org.json.JSONObject


class PeriodicBackgroundNotification(private val context: Context,
                                     workerParameters: WorkerParameters)
    : Worker(context, workerParameters) {

    private val TAG = "PeriodicNotification"

    override fun doWork(): Result {
        val newsItem = getNewsData()
        if (newsItem !=null){
            Log.d(TAG, "notification successful")
            Log.d(TAG, "new title: ${newsItem.title}")
            showNotification(newsItem, context)
            return  Result.success()
        }
        Log.d(TAG, "notification failed")
        return Result.failure()
    }

    private fun getNewsData(): NewsItem? {
        val imageUrl = "https://newsapi.org/v2/top-headlines?country=in&category=General&apiKey="+
                BuildConfig.API_KEY

        var newsItem: NewsItem?

        val anrRequest: ANRequest<*> = AndroidNetworking.get(imageUrl)
            .setPriority(Priority.LOW)
            .build()

        val response: ANResponse<*> = anrRequest.executeForJSONObject()

        if (response.isSuccess) {
            Log.d(TAG, "inside success response")
            val jsonObject: JSONObject = response.result as JSONObject
            val responseJsonArray: JSONArray = jsonObject.getJSONArray("articles")
            val articleObject = responseJsonArray.getJSONObject(0)
            val title = articleObject.getString("title")
            val desc = articleObject.getString("description")

            newsItem = NewsItem(title, desc, "", "", "", false)

        } else {
            newsItem = null
        }
        return newsItem
    }

    private fun showNotification(newsItem: NewsItem, context: Context) {

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "News Update"
            val descriptionText = "CHANNEL_FOR_UPPER_API_LEVEL"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("CHANNEL_ID_PERIOD_WORK", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val notification = NotificationCompat.Builder(context, "CHANNEL_ID_PERIOD_WORK").apply {
            setContentIntent(pendingIntent)
        }
        notification.setContentTitle("News Update")
        notification.setContentText(newsItem.title)
        notification.priority = NotificationCompat.PRIORITY_HIGH
        notification.setCategory(NotificationCompat.CATEGORY_ALARM)
        notification.setSmallIcon(R.drawable.dailynews_app_icon)
        notification.setAutoCancel(true)
        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        notification.setSound(sound)
        val vibrate = longArrayOf(0, 100, 200, 300)
        notification.setVibrate(vibrate)

        with(NotificationManagerCompat.from(context)) {
            notify(1, notification.build())
        }
    }
}