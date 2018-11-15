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
import android.widget.TextView


class PhotoGalleryFragment : Fragment() {
    private lateinit var mPhotoRecyclerView: RecyclerView
    private var mItems = ArrayList<GalleryItem>()
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
        setupAdapter()
        return v
    }

    private fun setupAdapter() {
        if (isAdded) {
            mPhotoRecyclerView.adapter = PhotoAdapter(mItems)
        }
    }

    private inner class PhotoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mTitleTextView: TextView = itemView as TextView

        fun bindGalleryItem(item: GalleryItem) {
            mTitleTextView.text = item.toString()
        }
    }

    private inner class PhotoAdapter(private val mGalleryItems: List<GalleryItem>) :
        RecyclerView.Adapter<PhotoHolder>() {
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PhotoHolder {
            val textView = TextView(activity)
            return PhotoHolder(textView)
        }

        override fun onBindViewHolder(photoHolder: PhotoHolder, position: Int) {
            val galleryItem = mGalleryItems[position]
            photoHolder.bindGalleryItem(galleryItem)
        }

        override fun getItemCount(): Int {
            return mGalleryItems.size
        }
    }

    private inner class FetchItemsTask : AsyncTask<Void, Void, ArrayList<GalleryItem>>() {
        override fun doInBackground(vararg params: Void): ArrayList<GalleryItem> {
            return FlickrFetchr()
                .getItems("https://www.baidu.com")
        }

        override fun onPostExecute(result: ArrayList<GalleryItem>?) {
            mItems = result as ArrayList<GalleryItem>
            setupAdapter()
        }
    }

    companion object {
        private const val TAG = "PhotoGalleryFragment"
        fun newInstance(): Fragment {
            return PhotoGalleryFragment()
        }
    }
}