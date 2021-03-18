package com.coreware.coreshipdriver.camera

import java.nio.ByteBuffer

/**
 * Based on https://github.com/googlesamples/mlkit/blob/master/android/material-showcase/app/src/main/java/com/google/mlkit/md/camera/FrameProcessor.kt
 *
 * An interface to process the input camera frame and perform detection on it.
 */
interface FrameProcessor {

    /** Processes the input frame with the underlying detector.  */
    fun process(data: ByteBuffer, frameMetadata: FrameMetadata, graphicOverlay: GraphicOverlay)

    /** Stops the underlying detector and release resources.  */
    fun stop()
}