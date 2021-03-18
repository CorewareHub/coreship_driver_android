package com.coreware.coreshipdriver.barcodedetection

import android.graphics.Canvas
import android.graphics.Path
import com.coreware.coreshipdriver.camera.GraphicOverlay
import com.coreware.coreshipdriver.util.SharedPreferencesUtil
import com.google.mlkit.vision.barcode.Barcode

/**
 * Based on https://github.com/googlesamples/mlkit/blob/master/android/material-showcase/app/src/main/java/com/google/mlkit/md/barcodedetection/BarcodeConfirmingGraphic.kt
 *
 * Guides user to move camera closer to confirm the detected barcode.
 **/
internal class BarcodeConfirmingGraphic(overlay: GraphicOverlay, private val barcode: Barcode) :
        BarcodeGraphicBase(overlay) {

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        // Draws a highlighted path to indicate the current progress to meet size requirement.
        val sizeProgress = SharedPreferencesUtil.getProgressToMeetBarcodeSizeRequirement(overlay.context, overlay, barcode)
        val path = Path()
        if (sizeProgress > 0.95f) {
            // To have a completed path with all corners rounded.
            path.moveTo(boxRect.left, boxRect.top)
            path.lineTo(boxRect.right, boxRect.top)
            path.lineTo(boxRect.right, boxRect.bottom)
            path.lineTo(boxRect.left, boxRect.bottom)
            path.close()
        } else {
            path.moveTo(boxRect.left, boxRect.top + boxRect.height() * sizeProgress)
            path.lineTo(boxRect.left, boxRect.top)
            path.lineTo(boxRect.left + boxRect.width() * sizeProgress, boxRect.top)

            path.moveTo(boxRect.right, boxRect.bottom - boxRect.height() * sizeProgress)
            path.lineTo(boxRect.right, boxRect.bottom)
            path.lineTo(boxRect.right - boxRect.width() * sizeProgress, boxRect.bottom)
        }
        canvas.drawPath(path, pathPaint)
    }
}