package com.example.demo.photogallery

import android.content.Intent
import android.app.IntentService
import android.content.Context
import android.util.Log
import android.net.ConnectivityManager
import android.os.SystemClock
import android.app.AlarmManager
import android.app.PendingIntent
import java.util.concurrent.TimeUnit
import android.content.ClipData.newIntent




class PollService : IntentService(TAG) {
    override fun onHandleIntent(intent: Intent?) {
        if (!isNetworkAvailableAndConnected()) {
            return
        }
        val query = QueryPreferences.getStoredQuery(this)
        val lastResultId = QueryPreferences.getLastResultId(this)
        val items: List<GalleryItem> = FlickrFetchr(query).getItems("https://www.baidu.com/")

        if (items.isEmpty()) {
            return
        }
        val resultId = items[0].mId
        if (resultId == lastResultId) {
            Log.i(TAG, "Got an old result: $resultId")
        } else {
            Log.i(TAG, "Got a new result: $resultId")
        }
        QueryPreferences.setLastResultId(this, resultId)
    }

    private fun isNetworkAvailableAndConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val isNetworkAvailable = cm.activeNetworkInfo != null
        return isNetworkAvailable && cm.activeNetworkInfo.isConnected
    }

    companion object {
        private const val TAG = "PollService"
        private var POLL_INTERVAL_MS: Long = TimeUnit.MINUTES.toMillis(1)
        private fun newIntent(context: Context): Intent {
            return Intent(context, PollService::class.java)
        }

        fun setServiceAlarm(context: Context, isOn: Boolean) {
            val i = PollService.newIntent(context)
            val pi = PendingIntent.getService(context, 0, i, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (isOn) {
                alarmManager.setRepeating(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), POLL_INTERVAL_MS, pi
                )
            } else {
                alarmManager.cancel(pi)
                pi.cancel()
            }
        }

        fun isServiceAlarmOn(context: Context): Boolean {
            val i = PollService.newIntent(context)
            val pi = PendingIntent
                .getService(context, 0, i, PendingIntent.FLAG_NO_CREATE)
            return pi != null
        }
    }
}