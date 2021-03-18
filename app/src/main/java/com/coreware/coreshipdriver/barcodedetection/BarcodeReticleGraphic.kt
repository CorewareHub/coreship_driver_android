package com.coreware.coreshipdriver.barcodedetection

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.content.ContextCompat
import com.coreware.coreshipdriver.R
import com.coreware.coreshipdriver.camera.CameraReticleAnimator
import com.coreware.coreshipdriver.camera.GraphicOverlay

/**
 * Based on https://github.com/googlesamples/mlkit/blob/master/android/material-showcase/app/src/main/java/com/google/mlkit/md/barcodedetection/BarcodeReticleGraphic.kt
 *
 * A camera reticle that locates at the center of canvas to indicate the system is active but has
 * not detected a barcode yet.
 */
internal class BarcodeReticleGraphic(overlay: GraphicOverlay, private val animator: CameraReticleAnimator) :
        BarcodeGraphicBase(overlay) {

    private val ripplePaint: Paint
    private val rippleSizeOffset: Int
    private val rippleStrokeWidth: Int
    private val rippleAlpha: Int

    init {
        val resources = overlay.resources
        ripplePaint = Paint()
        ripplePaint.style = Paint.Style.STROKE
        ripplePaint.color = ContextCompat.getColor(context, R.color.reticle_ripple)
        rippleSizeOffset = 0//resources.getDimensionPixelOffset(R.dimen.barcode_reticle_ripple_size_offset)
        rippleStrokeWidth = resources.getDimensionPixelOffset(R.dimen.barcode_reticle_ripple_stroke_width)
        rippleAlpha = ripplePaint.alpha
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        // Draws the ripple to simulate the breathing animation effect.
        ripplePaint.alpha = (rippleAlpha * animator.rippleAlphaScale).toInt()
        ripplePaint.strokeWidth = rippleStrokeWidth * animator.rippleStrokeWidthScale
        val offset = rippleSizeOffset * animator.rippleSizeScale
        val rippleRect = RectF(
                boxRect.left - offset,
                boxRect.top - offset,
                boxRect.right + offset,
                boxRect.bottom + offset
        )
        canvas.drawRoundRect(rippleRect, boxCornerRadius, boxCornerRadius, ripplePaint)
    }
}