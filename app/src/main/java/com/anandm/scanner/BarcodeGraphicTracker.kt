package com.anandm.scanner

import android.content.Context
import androidx.annotation.UiThread
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.Tracker
import com.google.android.gms.vision.barcode.Barcode

/**
 * Generic tracker which is used for tracking or reading a barcode (and can really be used for
 * any type of item).  This is used to receive newly detected items, add a graphical representation
 * to an overlay, update the graphics as the item changes, and remove the graphics when the item
 * goes away.
 */
class BarcodeGraphicTracker internal constructor(context: Context?) : Tracker<Barcode?>() {
    private var mBarcodeUpdateListener: BarcodeUpdateListener? = null

    /**
     * Consume the item instance detected from an Activity or Fragment level by implementing the
     * BarcodeUpdateListener interface method onBarcodeDetected.
     */
    interface BarcodeUpdateListener {
        @UiThread
        fun onBarcodeDetected(barcode: Barcode?)
    }

    /**
     * Start tracking the detected item instance within the item overlay.
     */
    override fun onNewItem(id: Int, item: Barcode?) {
        mBarcodeUpdateListener!!.onBarcodeDetected(item)
    }

    /**
     * Update the position/characteristics of the item within the overlay.
     */
    override fun onUpdate(detectionResults: Detections<Barcode?>, item: Barcode?) {}

    /**
     * Hide the graphic when the corresponding object was not detected.  This can happen for
     * intermediate frames temporarily, for example if the object was momentarily blocked from
     * view.
     */
    override fun onMissing(detectionResults: Detections<Barcode?>) {}

    /**
     * Called when the item is assumed to be gone for good. Remove the graphic annotation from
     * the overlay.
     */
    override fun onDone() {}

    init {
        if (context is BarcodeUpdateListener) {
            mBarcodeUpdateListener = context
        } else {
            throw RuntimeException("Hosting activity must implement BarcodeUpdateListener")
        }
    }
}