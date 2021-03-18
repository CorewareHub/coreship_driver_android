package com.coreware.coreshipdriver.camera

/**
 * Based on https://github.com/googlesamples/mlkit/blob/master/android/material-showcase/app/src/main/java/com/google/mlkit/md/productsearch/Product.kt
 *
 * Information about a product.
 * */
data class Product internal constructor(val imageUrl: String, val title: String, val subtitle: String)