package com.coreware.coreshipdriver.camera

import android.graphics.Bitmap
import com.coreware.coreshipdriver.util.CameraUtil
import java.nio.ByteBuffer


/**
 * Based on https://github.com/googlesamples/mlkit/blob/master/android/material-showcase/app/src/main/java/com/google/mlkit/md/InputInfo.kt
 */
interface InputInfo {
    fun getBitmap(): Bitmap
}

class CameraInputInfo(
        private val frameByteBuffer: ByteBuffer,
        private val frameMetadata: FrameMetadata
) : InputInfo {

    private var bitmap: Bitmap? = null

    @Synchronized
    override fun getBitmap(): Bitmap {
        return bitmap ?: let {
            bitmap = CameraUtil.convertToBitmap(
                    frameByteBuffer, frameMetadata.width, frameMetadata.height, frameMetadata.rotation
            )
            bitmap!!
        }
    }
}

class BitmapInputInfo(private val bitmap: Bitmap) : InputInfo {
    override fun getBitmap(): Bitmap {
        return bitmap
    }
}