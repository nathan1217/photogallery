package com.example.demo.photogallery

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class FlickrFetchr {
    @Throws(IOException::class)
    fun getUrlBytes(urlSpec: String): ByteArray {
        val url = URL(urlSpec)
        val connection = url.openConnection() as HttpURLConnection
        try {
            val out = ByteArrayOutputStream()
            val inputStream = connection.inputStream
            if (HttpURLConnection.HTTP_OK != connection.responseCode) {
                throw IOException(
                    "${connection.responseMessage}: with $urlSpec"
                )
            }
            val buffer = ByteArray(1024)
            var bytesRead = inputStream.read(buffer)
            while (bytesRead > 0) {
                out.write(buffer, 0, bytesRead)
                bytesRead = inputStream.read(buffer)
            }
            out.close()
            return out.toByteArray()
        } finally {
            connection.disconnect()
        }
    }

    @Throws(IOException::class)
    fun getUrlString(urlSpec: String): String {
        return String(getUrlBytes(urlSpec))
    }

    @Throws(IOException::class)
    fun getItems(urlSpec: String): ArrayList<GalleryItem> {
        val items = ArrayList<GalleryItem>()
        getUrlString(urlSpec)
        for (i in 1..20) {
            items.add(GalleryItem(i.toString(), "mCaption_$i", """mUrl_$i"""))
        }
        return items
    }
}