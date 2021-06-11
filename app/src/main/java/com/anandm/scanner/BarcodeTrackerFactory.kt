package com.anandm.scanner

import android.content.Context
import com.google.android.gms.vision.MultiProcessor
import com.google.android.gms.vision.Tracker
import com.google.android.gms.vision.barcode.Barcode
import javax.inject.Inject

/**
 * Factory for creating a tracker and associated graphic to be associated with a new barcode.  The
 * multi-processor uses this factory to create barcode trackers as needed -- one for each barcode.
 */
class BarcodeTrackerFactory : MultiProcessor.Factory<Barcode> {
    @JvmField
    @Inject
    var context: Context? = null
    override fun create(barcode: Barcode): Tracker<Barcode> {
        return BarcodeGraphicTracker(context)
    }
}