package com.coreware.coreshipdriver.barcodedetection

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Based on https://github.com/googlesamples/mlkit/blob/master/android/material-showcase/app/src/main/java/com/google/mlkit/md/barcodedetection/BarcodeField.kt
 *
 * Information about a barcode field.
 * */
@Parcelize
data class BarcodeField(val label: String, val value: String) : Parcelable
