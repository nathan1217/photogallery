package com.example.demo.photogallery

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import java.io.IOException
import android.widget.TextView
import android.graphics.drawable.Drawable
import android.os.Handler


class PhotoGalleryFragment : Fragment() {
    private lateinit var mPhotoRecyclerView: RecyclerView
    private var mItems = ArrayList<GalleryItem>()
    private lateinit var mThumbnailDownloader: ThumbnailDownloader<PhotoHolder>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        FetchItemsTask().execute()
        var responseHandler: Handler = Handler()
        mThumbnailDownloader = ThumbnailDownloader<PhotoHolder>(responseHandler)


        mThumbnailDownloader.setThumbnailDownloadListener(
            object : ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder> {
                override fun onThumbnailDownloaded(photoHolder: PhotoHolder, bitmap: Bitmap) {
                    var drawable: Drawable = BitmapDrawable(resources, bitmap)
                    photoHolder.bindDrawable(drawable)
                }
            })
        mThumbnailDownloader.start()
        mThumbnailDownloader.looper


        Log.i(TAG, "Background thread started")
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v: View = inflater.inflate(R.layout.fragment_photo_gallery, container, false)
        mPhotoRecyclerView = v.findViewById(R.id.photo_recycler_view)
        mPhotoRecyclerView.layoutManager = GridLayoutManager(activity!!, 3) as RecyclerView.LayoutManager?
        setupAdapter()
        return v
    }

    override fun onDestroy() {
        super.onDestroy()
        mThumbnailDownloader.quit()
        Log.i(TAG, "Background thread destroyed")
    }

    private fun setupAdapter() {
        if (isAdded) {
            mPhotoRecyclerView.adapter = PhotoAdapter(mItems)
        }
    }

    private inner class PhotoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var mItemImageView: ImageView = itemView.findViewById(R.id.imageView) as ImageView

        fun bindDrawable(drawable: Drawable) {
            mItemImageView.setImageDrawable(drawable)
        }
    }

    private inner class PhotoAdapter(private val mGalleryItems: List<GalleryItem>) :
        RecyclerView.Adapter<PhotoHolder>() {
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PhotoHolder {
            val inflater = LayoutInflater.from(activity)
            val view = inflater.inflate(R.layout.item_list_gallery, viewGroup, false)
            return PhotoHolder(view)
        }

        override fun onBindViewHolder(photoHolder: PhotoHolder, position: Int) {
            val galleryItem = mGalleryItems[position]
            val placeholder = resources.getDrawable(R.mipmap.ic_launcher_round)
            photoHolder.bindDrawable(placeholder)
            mThumbnailDownloader.queueThumbnail(photoHolder, galleryItem.mUrl)
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