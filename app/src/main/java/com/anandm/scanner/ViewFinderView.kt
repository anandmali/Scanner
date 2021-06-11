package com.anandm.scanner

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import androidx.annotation.FloatRange
import androidx.annotation.Px

internal class ViewFinderView(private val context: Context) : View(
    context
) {
    private val mMaskPaint: Paint
    private val mFramePaint: Paint
    private val mPath: Path
    var frameRect: Rect? = null
        private set
    private var mFrameRatioWidth = 1f
    private var mFrameRatioHeight = 1f
    private val mFrameSize = 0.85f
    private var endY = 0
    private var revAnimation = false

    //Number of frames line should move after each rea draw on the canvas,
    // this is equable to speed of the line animation
    private val frames = 6
    override fun onDraw(canvas: Canvas) {
        val frame = frameRect ?: return
        val width = width
        val height = height
        val top = frame.top.toFloat()
        val left = frame.left.toFloat()
        val right = frame.right.toFloat()
        val bottom = frame.bottom.toFloat()
        val path = mPath
        path.reset()

        //Draw transparent filled gray view //####
        //Start left top
        path.moveTo(left, top)
        path.lineTo(right, top)
        path.lineTo(right, bottom)
        path.lineTo(left, bottom)
        path.lineTo(left, top)
        //Start middle
        path.moveTo(0f, 0f)
        path.lineTo(width.toFloat(), 0f)
        path.lineTo(width.toFloat(), height.toFloat())
        path.lineTo(0f, height.toFloat())
        path.lineTo(0f, 0f)
        canvas.drawPath(path, mMaskPaint)
        path.reset()

        //Draw box border line
        //Start left
        path.moveTo(left, top)
        path.lineTo(right, top)
        path.lineTo(right, bottom)
        path.lineTo(left, bottom)
        path.lineTo(left, top)
        canvas.drawPath(path, mFramePaint)

        // draw horizontal line
        val line = Paint()
        line.color = resources.getColor(R.color.teal_700)
        line.strokeWidth = java.lang.Float.valueOf(getInDp(2f).toFloat())

        // draw the line for animation
        if (endY >= bottom + frames) {
            revAnimation = true
        } else if (endY.toFloat() == top + frames) {
            revAnimation = false
        }

        // check if the line has reached to bottom
        if (revAnimation) {
            endY -= frames
        } else {
            endY += frames
        }
        canvas.drawLine(left, endY.toFloat(), right, endY.toFloat(), line)
        invalidate()
    }

    override fun onLayout(
        changed: Boolean, left: Int, top: Int, right: Int,
        bottom: Int
    ) {
        invalidateFrameRect(right - left, bottom - top)
    }

    fun setFrameAspectRatio(
        @FloatRange(from = 0, fromInclusive = false) ratioWidth: Float,
        @FloatRange(from = 0, fromInclusive = false) ratioHeight: Float
    ) {
        mFrameRatioWidth = ratioWidth
        mFrameRatioHeight = ratioHeight
        invalidateFrameRect()
        if (isLaidOut) {
            invalidate()
        }
    }

    fun setFrameThickness(@Px thickness: Int) {
        mFramePaint.strokeWidth = thickness.toFloat()
        if (isLaidOut) {
            invalidate()
        }
    }

    private fun invalidateFrameRect(width: Int = getWidth(), height: Int = getHeight()) {
        if (width > 0 && height > 0) {
            val viewAR = width.toFloat() / height.toFloat()
            val frameAR = mFrameRatioWidth / mFrameRatioHeight
            val frameWidth: Int
            val frameHeight: Int
            if (viewAR <= frameAR) {
                frameWidth = Math.round(width * mFrameSize)
                frameHeight = Math.round(frameWidth / frameAR)
            } else {
                frameHeight = Math.round(height * mFrameSize)
                frameWidth = Math.round(frameHeight * frameAR)
            }
            val frameLeft = (width - frameWidth) / 2
            val frameTop = (height - frameHeight) / 2
            frameRect = Rect(frameLeft, frameTop, frameLeft + frameWidth, frameTop + frameHeight)
            endY = frameTop
        }
    }

    private fun getInDp(value: Float): Int {
        val density = context.resources.displayMetrics.density
        return Math.round(density * value)
    }

    init {
        mMaskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mMaskPaint.style = Paint.Style.FILL
        mMaskPaint.color = resources.getColor(R.color.scanner_fill_color)
        mFramePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mFramePaint.style = Paint.Style.STROKE
        mFramePaint.color = resources.getColor(R.color.scanner_box_color)
        val path = Path()
        path.fillType = Path.FillType.EVEN_ODD
        mPath = path
    }
}