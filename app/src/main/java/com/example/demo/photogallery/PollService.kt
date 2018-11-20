package com.example.demo.photogallery

import android.content.Intent
import android.app.IntentService
import android.content.Context
import android.util.Log
import android.net.ConnectivityManager


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
        fun newIntent(context: Context): Intent {
            return Intent(context, PollService::class.java)
        }
    }
}