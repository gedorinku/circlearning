package com.kurume_nct.studybattle.tools

import android.content.Context
import android.graphics.Matrix
import android.net.Uri
import android.util.AttributeSet
import android.widget.ImageView
import android.view.ScaleGestureDetector
import android.view.MotionEvent
import com.kurume_nct.studybattle.R


/**
 * Created by hanah on 10/12/2017.
 */
class CustomImageView : ImageView {
    private var scaleGestureDetector: ScaleGestureDetector? = null
    private val SCALE_MAX = 3.0f
    private val SCALE_MIN = 0.5f
    private val PINCH_SENSITIVITY = 5.0f

    private val simpleOnScaleGestureListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        internal var focusX: Float = 0.toFloat()
        internal var focusY: Float = 0.toFloat()

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            var scaleFactor = 1.0f
            val previousScale = getMatrixValue(Matrix.MSCALE_Y)

            if (detector.scaleFactor >= 1.0f) {
                scaleFactor = 1 + (detector.scaleFactor - 1) / (previousScale * PINCH_SENSITIVITY)
            } else {
                scaleFactor = 1 - (1 - detector.scaleFactor) / (previousScale * PINCH_SENSITIVITY)
            }

            val scale = scaleFactor * previousScale

            if (scale < SCALE_MIN) {
                return false
            }

            if (scale > SCALE_MAX) {
                return false
            }

            matrix!!.postScale(scaleFactor, scaleFactor, focusX, focusY)

            invalidate()

            return super.onScale(detector)

        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            focusX = detector.focusX
            focusY = detector.focusY
            return super.onScaleBegin(detector)
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            super.onScaleEnd(detector)
        }

    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private lateinit var uri: Uri

    fun setUri(uri: Uri){
        this.uri = uri
    }

    private fun init(context: Context) {
        setImageURI(uri)
        scaleType = ImageView.ScaleType.MATRIX
        scaleGestureDetector = ScaleGestureDetector(context, simpleOnScaleGestureListener)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        imageMatrix = matrix
        scaleGestureDetector!!.onTouchEvent(event)
        return scaleGestureDetector!!.onTouchEvent(event)
    }

    private fun getMatrixValue(index: Int): Float {

        val values = FloatArray(9)
        matrix.getValues(values)

        return values[index]
    }

}