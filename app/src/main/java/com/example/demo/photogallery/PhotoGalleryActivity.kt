package com.example.demo.photogallery

import android.support.v4.app.Fragment

class PhotoGalleryActivity : BaseFragmentActivity() {
    override fun createFragment(): Fragment {
        return PhotoGalleryFragment.newInstance()
    }
}
