package com.coreware.coreshipdriver.camera

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Rect
import com.coreware.coreshipdriver.R
import com.coreware.coreshipdriver.util.CameraUtil

/**
 * Based on https://github.com/googlesamples/mlkit/blob/master/android/material-showcase/app/src/main/java/com/google/mlkit/md/productsearch/SearchedObject.kt
 *
 * Hosts the detected object info and its search result.
 * */
class SearchedObject(
        resources: Resources,
        private val detectedObject: DetectedObjectInfo,
        val productList: List<Product>
) {

    private val objectThumbnailCornerRadius: Int = resources.getDimensionPixelOffset(R.dimen.bounding_box_corner_radius)
    private var objectThumbnail: Bitmap? = null

    val objectIndex: Int
        get() = detectedObject.objectIndex

    val boundingBox: Rect
        get() = detectedObject.boundingBox

    @Synchronized
    fun getObjectThumbnail(): Bitmap = objectThumbnail ?: let {
        CameraUtil.getCornerRoundedBitmap(detectedObject.getBitmap(), objectThumbnailCornerRadius)
                .also { objectThumbnail = it }
    }
}