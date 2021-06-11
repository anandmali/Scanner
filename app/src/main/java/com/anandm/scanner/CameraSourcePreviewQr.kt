package com.anandm.scanner

import android.Manifest
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import androidx.annotation.RequiresPermission
import java.io.IOException

class CameraSourcePreviewQr(private val mContext: Context, attrs: AttributeSet?) : ViewGroup(
    mContext, attrs
) {
    private val mSurfaceView: SurfaceView
    private var mStartRequested = false
    private var mSurfaceAvailable = false
    private var mCameraSourceQr: CameraSourceQr? = null
    private var mViewFinderView: ViewFinderView? = null
    @RequiresPermission(Manifest.permission.CAMERA)
    @Throws(IOException::class, SecurityException::class)
    fun start(cameraSource: CameraSourceQr?) {
        if (cameraSource == null) {
            stop()
        }
        mCameraSourceQr = cameraSource
        if (mCameraSourceQr != null) {
            mStartRequested = true
            startIfReady()
        }
    }

    fun stop() {
        if (mCameraSourceQr != null) {
            mCameraSourceQr!!.stop()
        }
    }

    fun release() {
        if (mCameraSourceQr != null) {
            mCameraSourceQr!!.release()
            mCameraSourceQr = null
        }
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    @Throws(IOException::class, SecurityException::class)
    private fun startIfReady() {
        if (mStartRequested && mSurfaceAvailable) {
            mCameraSourceQr!!.start(mSurfaceView.holder)
            mStartRequested = false
        }
    }

    private inner class SurfaceCallback : SurfaceHolder.Callback {
        override fun surfaceCreated(surface: SurfaceHolder) {
            mSurfaceAvailable = true
            try {
                startIfReady()
            } catch (se: SecurityException) {
                Log.e(TAG, "Do not have permission to start the camera", se)
            } catch (e: IOException) {
                Log.e(TAG, "Could not start camera source.", e)
            }
        }

        override fun surfaceDestroyed(surface: SurfaceHolder) {
            mSurfaceAvailable = false
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        performLayout(right - left, bottom - top)
    }

    private fun initialize(context: Context) {
        mSurfaceView.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        mViewFinderView = ViewFinderView(context)
        mViewFinderView!!.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        val density = context.resources.displayMetrics.density
        mViewFinderView!!.setFrameAspectRatio(
            getInDp(DEFAULT_FRAME_ASPECT_RATIO_WIDTH).toFloat(), getInDp(
                DEFAULT_FRAME_ASPECT_RATIO_HEIGHT
            ).toFloat()
        )
        mViewFinderView!!.setFrameThickness(Math.round(DEFAULT_FRAME_THICKNESS_DP * density))
        addView(mSurfaceView)
        addView(mViewFinderView)
    }

    private fun performLayout(width: Int, height: Int) {
        mSurfaceView.layout(0, 0, width, height)
        mViewFinderView!!.layout(0, 0, width, height)
    }

    private fun getInDp(value: Float): Int {
        val density = mContext.resources.displayMetrics.density
        return Math.round(density * value)
    }

    companion object {
        private val TAG = CameraSourcePreviewQr::class.java.simpleName
        private const val DEFAULT_FRAME_THICKNESS_DP = 2f
        private const val DEFAULT_FRAME_ASPECT_RATIO_WIDTH = 1f
        private const val DEFAULT_FRAME_ASPECT_RATIO_HEIGHT = 1f
    }

    init {
        mSurfaceView = SurfaceView(mContext)
        mSurfaceView.holder.addCallback(SurfaceCallback())
        initialize(mContext)
    }
}