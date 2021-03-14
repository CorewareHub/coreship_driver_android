package com.coreware.coreshipdriver.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.ExecutionException

/**
 * View model for interacting with CameraX
 * Create an instance which interacts with the camera service via the application context
 */
class CameraXViewModel(application: Application) : AndroidViewModel(application) {
    private var cameraProviderLiveData: MutableLiveData<ProcessCameraProvider>? = null

    // Handle any error including cancellation here
    val processCameraProvider: LiveData<ProcessCameraProvider>
        get() {
            if (cameraProviderLiveData == null) {
                cameraProviderLiveData = MutableLiveData()
                val cameraProviderFuture = ProcessCameraProvider.getInstance(getApplication())
                cameraProviderFuture.addListener(
                        Runnable {
                            try {
                                cameraProviderLiveData!!.setValue(cameraProviderFuture.get())
                            } catch (e: ExecutionException) {
                                // Handle any errors (including cancellation) here.
                                Log.e(LOG_TAG, "Unhandled exception", e)
                            } catch (e: InterruptedException) {
                                Log.e(LOG_TAG, "Unhandled exception", e)
                            }
                        }, ContextCompat.getMainExecutor(getApplication())
                )
            }
            return cameraProviderLiveData!!
        }

    companion object {
        private const val LOG_TAG = "CameraXViewModel"
    }
}