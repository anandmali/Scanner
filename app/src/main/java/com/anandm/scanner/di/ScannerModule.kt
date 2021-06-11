package com.anandm.scanner.di

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Camera
import android.util.Log
import com.anandm.scanner.BarcodeCaptureActivity
import com.anandm.scanner.BarcodeTrackerFactory
import com.anandm.scanner.CameraSourceQr
import com.google.android.gms.vision.MultiProcessor
import com.google.android.gms.vision.barcode.BarcodeDetector
import dagger.Module
import dagger.Provides

@Module
class ScannerModule {
    @Provides
    fun provideScannerActivity(activity: BarcodeCaptureActivity): Context {
        return activity
    }

    @Provides
    fun provideDetector(context: Context): BarcodeDetector {
        // A barcode detector is created to track barcodes.  An associated multi-processor instance
        // is set to receive the barcode detection results, track the barcodes, and maintain
        // graphics for each barcode on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each barcode.
        val barcodeDetector = BarcodeDetector.Builder(context.applicationContext).build()
        val barcodeFactory = BarcodeTrackerFactory()
        barcodeDetector.setProcessor(MultiProcessor.Builder(barcodeFactory).build())
        return barcodeDetector
    }

    @Provides
    fun createCameraSource(context: Context, barcodeDetector: BarcodeDetector): CameraSourceQr {
        if (!barcodeDetector.isOperational) {
            // Note: The first time that an app using the barcode or face API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any barcodes
            // and/or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(TAG, "Detector dependencies are not yet available.")

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            val lowStorageFilter = IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW)
            val hasLowStorage = context.registerReceiver(null, lowStorageFilter) != null
            if (hasLowStorage) {
                Log.w(TAG, "Ocr dependencies cannot be downloaded due to low device storage")
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the barcode detector to detect small barcodes
        // at long distances.
        val builder = CameraSourceQr.Builder(context.applicationContext, barcodeDetector)
            .setFacing(CameraSourceQr.CAMERA_FACING_BACK)
            .setRequestedPreviewSize(1600, 1024)
            .setRequestedFps(15.0f)
            .setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)
        return builder.build()
    }

    companion object {
        private val TAG = ScannerModule::class.java.simpleName
    }
}