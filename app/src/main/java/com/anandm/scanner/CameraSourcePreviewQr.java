package com.anandm.scanner;

import android.Manifest;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

import java.io.IOException;

public class CameraSourcePreviewQr extends ViewGroup {

    private static final String TAG = CameraSourcePreviewQr.class.getSimpleName();

    private final Context mContext;
    private final SurfaceView mSurfaceView;
    private boolean mStartRequested;
    private boolean mSurfaceAvailable;
    private CameraSourceQr mCameraSourceQr;
    private ViewFinderView mViewFinderView;

    private static final float DEFAULT_FRAME_THICKNESS_DP = 2f;
    private static final float DEFAULT_FRAME_ASPECT_RATIO_WIDTH = 1f;
    private static final float DEFAULT_FRAME_ASPECT_RATIO_HEIGHT = 1f;

    public CameraSourcePreviewQr(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mStartRequested = false;
        mSurfaceAvailable = false;

        mSurfaceView = new SurfaceView(context);
        mSurfaceView.getHolder().addCallback(new SurfaceCallback());
        initialize(context);
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    public void start(CameraSourceQr cameraSource) throws IOException, SecurityException {
        if (cameraSource == null) {
            stop();
        }

        mCameraSourceQr = cameraSource;

        if (mCameraSourceQr != null) {
            mStartRequested = true;
            startIfReady();
        }
    }

    public void stop() {
        if (mCameraSourceQr != null) {
            mCameraSourceQr.stop();
        }
    }

    public void release() {
        if (mCameraSourceQr != null) {
            mCameraSourceQr.release();
            mCameraSourceQr = null;
        }
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    private void startIfReady() throws IOException, SecurityException {
        if (mStartRequested && mSurfaceAvailable) {
            mCameraSourceQr.start(mSurfaceView.getHolder());
            mStartRequested = false;
        }
    }

    private class SurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder surface) {
            mSurfaceAvailable = true;
            try {
                startIfReady();
            } catch (SecurityException se) {
                Log.e(TAG, "Do not have permission to start the camera", se);
            } catch (IOException e) {
                Log.e(TAG, "Could not start camera source.", e);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surface) {
            mSurfaceAvailable = false;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        performLayout(right - left, bottom - top);
    }

    private void initialize(@NonNull final Context context) {
        mSurfaceView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mViewFinderView = new ViewFinderView(context);
        mViewFinderView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        final float density = context.getResources().getDisplayMetrics().density;
        mViewFinderView.setFrameAspectRatio(getInDp(DEFAULT_FRAME_ASPECT_RATIO_WIDTH), getInDp(DEFAULT_FRAME_ASPECT_RATIO_HEIGHT));
        mViewFinderView.setFrameThickness(Math.round(DEFAULT_FRAME_THICKNESS_DP * density));

        addView(mSurfaceView);
        addView(mViewFinderView);
    }


    private void performLayout(final int width, final int height) {

        mSurfaceView.layout(0, 0, width, height);
        mViewFinderView.layout(0, 0, width, height);

    }

    private int getInDp(float value) {
        final float density = mContext.getResources().getDisplayMetrics().density;
        return Math.round(density * value);
    }
}
