package com.coreware.coreshipdriver.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.coreware.coreshipdriver.R
import com.coreware.coreshipdriver.ui.viewmodels.CameraXViewModel
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Based on https://github.com/android/camera-samples/blob/main/CameraXBasic/app/src/main/java/com/android/example/cameraxbasic/fragments/CameraFragment.kt
 * for CameraX fragment implementation and https://proandroiddev.com/building-barcode-qr-code-scanner-for-android-using-google-ml-kit-and-camerax-220b2852589e
 * for barcode integration with CameraX
 */
class ExpressPickupFragment : Fragment() {

    private lateinit var previewView: PreviewView

    private var cameraProvider: ProcessCameraProvider? = null
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private var cameraSelector: CameraSelector? = null
    private var preview: Preview? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (isCameraPermissionGranted()) {
            // start camera
            setupCamera()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), PERMISSION_CAMERA_REQUEST)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_express_pickup, container, false)
        previewView = rootView.findViewById(R.id.preview_view)
        return rootView
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_CAMERA_REQUEST) {
            if (isCameraPermissionGranted()) {
                // start camera
                setupCamera()
            } else {
                Log.w(LOG_TAG, "Camera permission not granted")
                Toast.makeText(requireContext(), "Camera permission not granted", Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    /* Private methods */

    private fun setupCamera() {
        cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

        ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))
                .get(CameraXViewModel::class.java)
                .processCameraProvider
                .observe(this, Observer { provider: ProcessCameraProvider?->
                    cameraProvider = provider
                    if (isCameraPermissionGranted()) {
                        bindCameraUseCases()
                    } else {
                        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), PERMISSION_CAMERA_REQUEST)
                    }
                })
    }

    private fun bindCameraUseCases() {
        if (cameraProvider == null) {
            Log.w(LOG_TAG, "CameraProvider was null when binding use cases")
            return
        }
        if (preview != null) {
            cameraProvider!!.unbind(preview)
        }

        val metrics = DisplayMetrics().also { previewView.display.getRealMetrics(it) }
        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        val rotation = previewView.display.rotation
        preview = Preview.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(rotation)
                .build()
        preview!!.setSurfaceProvider(previewView!!.surfaceProvider)

        try {
            cameraProvider!!.bindToLifecycle(this, cameraSelector!!, preview)
        } catch (illegalStateException: IllegalStateException) {
            Log.e(LOG_TAG, illegalStateException.localizedMessage, illegalStateException)
        } catch (illegalArgumentException: IllegalArgumentException) {
            Log.e(LOG_TAG, illegalArgumentException.localizedMessage, illegalArgumentException)
        }
    }

    /**
     *  Detecting the most suitable ratio for dimensions provided in @params by counting absolute
     *  of preview ratio to one of the provided values.
     *
     *  @param width - preview width
     *  @param height - preview height
     *  @return suitable aspect ratio
     */
    private fun aspectRatio(width: Int, height: Int): Int {
        val ratio = max(width, height).toDouble() / min(width, height)
        if (abs(ratio - RATIO_4_3_VALUE) <= abs(ratio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val LOG_TAG = ExpressPickupFragment::class.java.name
        private const val PERMISSION_CAMERA_REQUEST = 1
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }

}