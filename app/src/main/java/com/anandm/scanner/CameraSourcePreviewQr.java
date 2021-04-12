package com.anandm.scanner;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private static final float BUTTON_SIZE_DP = 56f;
    private static final float AADHAAR_IMAGE_HEIGHT_DP = 56f;
    private static final float AADHAAR_IMAGE_WIDTH_DP = 72;
    private static final int DEFAULT_FLASH_BUTTON_VISIBILITY = VISIBLE;
    private static final int DEFAULT_FLASH_BUTTON_COLOR = Color.WHITE;

    private int mButtonSize;
    private int mDemoImageHeight;
    private int mDemoImageWidth;
    private ImageView mFlashButton;
    private TextView mTxtScannerNote;
    private ImageView mImgAadhaarDemo;
    private boolean isFlashEnabled;

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

        mButtonSize = Math.round(density * BUTTON_SIZE_DP);

        boolean hasFlash = mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (hasFlash) {
            mFlashButton = new ImageView(context);
            mFlashButton.setLayoutParams(new LayoutParams(mButtonSize, mButtonSize));
            mFlashButton.setScaleType(ImageView.ScaleType.CENTER);
            mFlashButton.setImageResource(R.drawable.ic_code_scanner_flash_on);
            mFlashButton.setColorFilter(DEFAULT_FLASH_BUTTON_COLOR);
            mFlashButton.setVisibility(DEFAULT_FLASH_BUTTON_VISIBILITY);
            mFlashButton.setOnClickListener(new FlashButtonClickLIstner());
        }

        mTxtScannerNote = new TextView(context);
        mTxtScannerNote.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        mTxtScannerNote.setTextColor(Color.WHITE);
        mTxtScannerNote.setVisibility(VISIBLE);
        mTxtScannerNote.setText(getResources().getString(R.string.txt_aadhaar_scan_note));
        mTxtScannerNote.setTextSize(14);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mTxtScannerNote.setGravity(Gravity.CENTER_HORIZONTAL);
        mTxtScannerNote.setLayoutParams(layoutParams);

        mImgAadhaarDemo = new ImageView(context);
        mImgAadhaarDemo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mImgAadhaarDemo.setImageResource(R.drawable.ic_aadhaar_scanner_demo);
        mImgAadhaarDemo.setVisibility(DEFAULT_FLASH_BUTTON_VISIBILITY);
        mDemoImageHeight = Math.round(density * AADHAAR_IMAGE_HEIGHT_DP);
        mDemoImageWidth = Math.round(density * AADHAAR_IMAGE_WIDTH_DP);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(mDemoImageWidth, mDemoImageHeight);
        mImgAadhaarDemo.setLayoutParams(layoutParams1);

        addView(mSurfaceView);
        addView(mViewFinderView);
        addView(mTxtScannerNote);
        addView(mImgAadhaarDemo);
        if (mFlashButton != null) {
            addView(mFlashButton);
        }
    }


    private void performLayout(final int width, final int height) {

        mSurfaceView.layout(0, 0, width, height);
        mViewFinderView.layout(0, 0, width, height);
        final int buttonSize = mButtonSize;

        if (mFlashButton != null) {
            mFlashButton.layout(width - buttonSize, 0, width, buttonSize);
        }

        if (mViewFinderView.getFrameRect() != null) {
            mTxtScannerNote.layout(0, mViewFinderView.getFrameRect().getTop() - getInDp((88f + 16f)), width, (mViewFinderView.getFrameRect().getTop() - getInDp(88f)));
            mImgAadhaarDemo.layout(0, (mViewFinderView.getFrameRect().getTop() - getInDp(72f)), width, mViewFinderView.getFrameRect().getTop() - getInDp(16f));
        }
    }

    private int getInDp(float value) {
        final float density = mContext.getResources().getDisplayMetrics().density;
        return Math.round(density * value);
    }

    private class FlashButtonClickLIstner implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (mCameraSourceQr != null) {
                if (!isFlashEnabled) {
                    isFlashEnabled = mCameraSourceQr.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                } else {
                    isFlashEnabled = false;
                    mCameraSourceQr.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                }
            }
        }
    }
}
