package com.anandm.scanner

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import com.anandm.scanner.BarcodeGraphicTracker.BarcodeUpdateListener
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.vision.barcode.Barcode
import dagger.android.support.DaggerAppCompatActivity
import java.io.IOException
import javax.inject.Inject

class BarcodeCaptureActivity : DaggerAppCompatActivity(), BarcodeUpdateListener,
    View.OnClickListener, OnRequestPermissionsResultCallback {
    @JvmField
    @Inject
    var mCameraSource: CameraSourceQr? = null
    private var mPreview: CameraSourcePreviewQr? = null
    private lateinit var mImgClose: ImageView;
    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.barcode_capture)
        mPreview = findViewById(R.id.preview)
        mImgClose = findViewById(R.id.imgClose)
        mImgClose.setOnClickListener(this)
        val rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (rc == PackageManager.PERMISSION_GRANTED) {
            // createCameraSource(); TODO handle permission before creating source
        } else {
            requestCameraPermission()
        }
    }

    override fun onClick(v: View) {
        if (v.id == R.id.imgClose) {
            val data = Intent()
            setResult(RESULT_CANCELED, data)
            finish()
        }
    }

    private fun requestCameraPermission() {
        val permissions = arrayOf(Manifest.permission.CAMERA)
        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            )
        ) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM)
            return
        }
        val listener = View.OnClickListener { view: View? ->
            ActivityCompat.requestPermissions(
                this,
                permissions,
                RC_HANDLE_CAMERA_PERM
            )
        }
        findViewById<View>(R.id.topLayout).setOnClickListener(listener)
    }

    /**
     * Restarts the camera.
     */
    override fun onResume() {
        super.onResume()
        startCameraSource()
    }

    /**
     * Stops the camera.
     */
    override fun onPause() {
        super.onPause()
        if (mPreview != null) {
            mPreview!!.stop()
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    override fun onDestroy() {
        super.onDestroy()
        if (mPreview != null) {
            mPreview!!.release()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: $requestCode")
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }
        if (grantResults.size != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source")
            // we have permission, so create the camerasource
            // createCameraSource(); TODO
            return
        }
        Log.e(TAG, "Permission not granted")
        val listener =
            DialogInterface.OnClickListener { dialog: DialogInterface?, id: Int -> finish() }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Scanner demo")
            .setMessage(R.string.no_camera_permission)
            .setPositiveButton(R.string.ok, listener)
            .show()
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    @Throws(SecurityException::class)
    private fun startCameraSource() {
        // check that the device has play services available.
        val code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
            applicationContext
        )
        if (code != ConnectionResult.SUCCESS) {
            val dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS)
            dlg.show()
        }
        if (mCameraSource != null) {
            try {
                mPreview!!.start(mCameraSource)
            } catch (e: IOException) {
                Log.e(TAG, "Unable to start camera source.", e)
            } finally {
                mCameraSource!!.release()
                mCameraSource = null
            }
        }
    }

    override fun onBarcodeDetected(barcode: Barcode?) {
        if (barcode != null) {
            Log.d(TAG, barcode.rawValue)
        }
    }

    companion object {
        private val TAG = BarcodeCaptureActivity::class.java.simpleName

        // intent request codes
        private const val RC_HANDLE_GMS = 1
        private const val RC_HANDLE_CAMERA_PERM = 2
    }
}