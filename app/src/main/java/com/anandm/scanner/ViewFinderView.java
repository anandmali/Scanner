package com.anandm.scanner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Px;

final class ViewFinderView extends View {

    private final Context context;
    private final Paint mMaskPaint;
    private final Paint mFramePaint;
    private final Path mPath;
    private Rect mFrameRect;
    private float mFrameRatioWidth = 1f;
    private float mFrameRatioHeight = 1f;
    private final float mFrameSize = 0.85f;
    private int endY;
    private boolean revAnimation;

    //Number of frames line should move after each rea draw on the canvas,
    // this is equable to speed of the line animation
    private final int frames = 6;


    public ViewFinderView(@NonNull final Context context) {
        super(context);
        this.context = context;

        mMaskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMaskPaint.setStyle(Paint.Style.FILL);
        mMaskPaint.setColor(getResources().getColor(R.color.scanner_fill_color));

        mFramePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFramePaint.setStyle(Paint.Style.STROKE);
        mFramePaint.setColor(getResources().getColor(R.color.scanner_box_color));

        final Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        mPath = path;
    }

    @Override
    protected void onDraw(@NonNull final Canvas canvas) {
        final Rect frame = mFrameRect;
        if (frame == null) {
            return;
        }
        final int width = getWidth();
        final int height = getHeight();
        final float top = frame.getTop();
        final float left = frame.getLeft();
        final float right = frame.getRight();
        final float bottom = frame.getBottom();
        final Path path = mPath;
        path.reset();

        //Draw transparent filled gray view //####
        //Start left top
        path.moveTo(left, top);
        path.lineTo(right, top);
        path.lineTo(right, bottom);
        path.lineTo(left, bottom);
        path.lineTo(left, top);
        //Start middle
        path.moveTo(0, 0);
        path.lineTo(width, 0);
        path.lineTo(width, height);
        path.lineTo(0, height);
        path.lineTo(0, 0);
        canvas.drawPath(path, mMaskPaint);
        path.reset();

        //Draw box border line
        //Start left
        path.moveTo(left, top);
        path.lineTo(right, top);
        path.lineTo(right, bottom);
        path.lineTo(left, bottom);
        path.lineTo(left, top);
        canvas.drawPath(path, mFramePaint);

        // draw horizontal line
        Paint line = new Paint();
        line.setColor(getResources().getColor(R.color.teal_700));
        line.setStrokeWidth(Float.valueOf(getInDp(2f)));

        // draw the line for animation
        if (endY >= (bottom + frames)) {
            revAnimation = true;
        } else if (endY == top + frames) {
            revAnimation = false;
        }

        // check if the line has reached to bottom
        if (revAnimation) {
            endY -= frames;
        } else {
            endY += frames;
        }
        canvas.drawLine(left, endY, right, endY, line);

        invalidate();
    }

    @Override
    protected void onLayout(final boolean changed, final int left, final int top, final int right,
                            final int bottom) {
        invalidateFrameRect(right - left, bottom - top);
    }

    void setFrameAspectRatio(@FloatRange(from = 0, fromInclusive = false) final float ratioWidth,
                             @FloatRange(from = 0, fromInclusive = false) final float ratioHeight) {
        mFrameRatioWidth = ratioWidth;
        mFrameRatioHeight = ratioHeight;
        invalidateFrameRect();
        if (isLaidOut()) {
            invalidate();
        }
    }

    void setFrameThickness(@Px final int thickness) {
        mFramePaint.setStrokeWidth(thickness);
        if (isLaidOut()) {
            invalidate();
        }
    }

    public Rect getFrameRect() {
        return mFrameRect;
    }

    private void invalidateFrameRect() {
        invalidateFrameRect(getWidth(), getHeight());
    }

    private void invalidateFrameRect(final int width, final int height) {
        if (width > 0 && height > 0) {
            final float viewAR = (float) width / (float) height;
            final float frameAR = mFrameRatioWidth / mFrameRatioHeight;
            final int frameWidth;
            final int frameHeight;
            if (viewAR <= frameAR) {
                frameWidth = Math.round(width * mFrameSize);
                frameHeight = Math.round(frameWidth / frameAR);
            } else {
                frameHeight = Math.round(height * mFrameSize);
                frameWidth = Math.round(frameHeight * frameAR);
            }
            final int frameLeft = (width - frameWidth) / 2;
            final int frameTop = (height - frameHeight) / 2;
            mFrameRect = new Rect(frameLeft, frameTop, frameLeft + frameWidth, frameTop + frameHeight);
            endY = frameTop;
        }
    }

    private int getInDp(float value) {
        final float density = context.getResources().getDisplayMetrics().density;
        return Math.round(density * value);
    }
}
