package com.example.demo.photogallery

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.os.AsyncTask
import android.util.Log
import java.io.IOException


class PhotoGalleryFragment : Fragment() {
    private lateinit var mPhotoRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        FetchItemsTask().execute()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v: View = inflater.inflate(R.layout.fragment_photo_gallery, container, false)
        mPhotoRecyclerView = v.findViewById(R.id.photo_recycler_view)
        mPhotoRecyclerView.layoutManager = GridLayoutManager(activity!!, 3)
        return v
    }

    private inner class FetchItemsTask : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void): Void? {
            try {
                val result = FlickrFetchr()
                    .getUrlString("https://www.baidu.com")
                Log.i(TAG, "Fetched contents of URL: $result")
            } catch (ioe: IOException) {
                Log.e(TAG, "Failed to fetch URL: ", ioe)
            }

            return null
        }
    }

    companion object {
        private const val TAG = "PhotoGalleryFragment"
        fun newInstance(): Fragment {
            return PhotoGalleryFragment()
        }
    }
}