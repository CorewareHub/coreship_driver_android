package com.coreware.coreshipdriver.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.coreware.coreshipdriver.R
import com.coreware.coreshipdriver.barcodedetection.BarcodeResultFragment
import com.coreware.coreshipdriver.camera.CameraSource
import com.coreware.coreshipdriver.camera.CameraSourcePreview
import com.coreware.coreshipdriver.camera.GraphicOverlay
import com.coreware.coreshipdriver.camera.WorkflowModel
import com.google.android.gms.common.internal.Objects
import com.coreware.coreshipdriver.barcodedetection.BarcodeField
import com.coreware.coreshipdriver.barcodedetection.BarcodeProcessor
import java.io.IOException
import java.util.*

/**
 * Based on https://github.com/googlesamples/mlkit/blob/master/android/material-showcase/app/src/main/java/com/google/mlkit/md/LiveBarcodeScanningActivity.kt
 * with additional input from
 * https://github.com/android/camera-samples/blob/main/CameraXBasic/app/src/main/java/com/android/example/cameraxbasic/fragments/CameraFragment.kt
 * for CameraX fragment implementation and https://proandroiddev.com/building-barcode-qr-code-scanner-for-android-using-google-ml-kit-and-camerax-220b2852589e
 * for barcode integration with CameraX
 */
class ExpressPickupFragment : Fragment() {

    private var cameraSource: CameraSource? = null
    private var preview: CameraSourcePreview? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var workflowModel: WorkflowModel? = null
    private var currentWorkflowState: WorkflowModel.WorkflowState? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_express_pickup, container, false)
        preview = rootView.findViewById(R.id.camera_preview)
        graphicOverlay = rootView.findViewById<GraphicOverlay>(R.id.camera_preview_graphic_overlay).apply {
            cameraSource = CameraSource(this)
        }

        var closeButton: View = rootView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(View.OnClickListener {
            requireActivity().onBackPressed()
        })

        setUpWorkflowModel()
        return rootView
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_CAMERA_REQUEST) {
            if (isCameraPermissionGranted()) {
                // start camera
            } else {
                Log.e(LOG_TAG, "no camera permission")
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onResume() {
        super.onResume()

        if (isCameraPermissionGranted()) {
            // startCamera
            workflowModel?.markCameraFrozen()
            currentWorkflowState = WorkflowModel.WorkflowState.NOT_STARTED
            if (cameraSource != null && graphicOverlay != null && workflowModel != null) {
                cameraSource?.setFrameProcessor(BarcodeProcessor(graphicOverlay!!, workflowModel!!))
            }
            workflowModel?.setWorkflowState(WorkflowModel.WorkflowState.DETECTING)
        } else {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    PERMISSION_CAMERA_REQUEST
            )
        }
    }

    override fun onPause() {
        super.onPause()
        currentWorkflowState = WorkflowModel.WorkflowState.NOT_STARTED
        stopCameraPreview()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource?.release()
        cameraSource = null
    }


    /* Private methods */

    private fun startCameraPreview() {
        val workflowModel = this.workflowModel ?: return
        val cameraSource = this.cameraSource ?: return
        if (!workflowModel.isCameraLive) {
            try {
                workflowModel.markCameraLive()
                preview?.start(cameraSource)
            } catch (e: IOException) {
                Log.e(LOG_TAG, "Failed to start camera preview!", e)
                cameraSource.release()
                this.cameraSource = null
            }
        }
    }

    private fun stopCameraPreview() {
        val workflowModel = this.workflowModel ?: return
        if (workflowModel.isCameraLive) {
            workflowModel.markCameraFrozen()
            preview?.stop()
        }
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun setUpWorkflowModel() {
        workflowModel = ViewModelProviders.of(this).get(WorkflowModel::class.java)

        // Observes the workflow state changes, if happens, update the overlay view indicators and
        // camera preview state.
        workflowModel!!.workflowState.observe(this, Observer { workflowState ->
            if (workflowState == null || Objects.equal(currentWorkflowState, workflowState)) {
                return@Observer
            }

            currentWorkflowState = workflowState
            Log.d(LOG_TAG, "Current workflow state: ${currentWorkflowState!!.name}")

            when (workflowState) {
                WorkflowModel.WorkflowState.DETECTING -> {
//                    promptChip?.visibility = View.VISIBLE
//                    promptChip?.setText(R.string.prompt_point_at_a_barcode)
                    startCameraPreview()
                }
                WorkflowModel.WorkflowState.CONFIRMING -> {
//                    promptChip?.visibility = View.VISIBLE
//                    promptChip?.setText(R.string.prompt_move_camera_closer)
                    startCameraPreview()
                }
                WorkflowModel.WorkflowState.SEARCHING -> {
//                    promptChip?.visibility = View.VISIBLE
//                    promptChip?.setText(R.string.prompt_searching)
                    stopCameraPreview()
                }
                WorkflowModel.WorkflowState.DETECTED, WorkflowModel.WorkflowState.SEARCHED -> {
//                    promptChip?.visibility = View.GONE
                    stopCameraPreview()
                }
//                else -> promptChip?.visibility = View.GONE
            }

//            val shouldPlayPromptChipEnteringAnimation = wasPromptChipGone && promptChip?.visibility == View.VISIBLE
//            promptChipAnimator?.let {
//                if (shouldPlayPromptChipEnteringAnimation && !it.isRunning) it.start()
//            }
        })

        workflowModel?.detectedBarcode?.observe(this, Observer { barcode ->
            if (barcode != null) {
                val barcodeFieldList = ArrayList<BarcodeField>()
                barcodeFieldList.add(BarcodeField("Raw Value", barcode.rawValue ?: ""))
                BarcodeResultFragment.show(requireActivity().supportFragmentManager, barcodeFieldList)
            }
        })
    }

    companion object {
        private val LOG_TAG = ExpressPickupFragment::class.java.name
        private const val PERMISSION_CAMERA_REQUEST = 1
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }

}