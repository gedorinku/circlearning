package com.kurume_nct.studybattle.tools

import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ImageView
import com.kurume_nct.studybattle.R


/**
 * Created by hanah on 10/20/2017.
 */
class LargeImageView(context: Context) : ImageView(context) {

    private var scaleGastureDetector: ScaleGestureDetector

    constructor(context: Context, attributeSet: AttributeSet): this(context)
    constructor(context: Context, attributeSet: AttributeSet, def: Int): this(context)

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        imageMatrix = matrix
        return scaleGastureDetector.onTouchEvent(event)
    }

    private val simpleOnScaleGestureListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        var focusX = 0F
        var focusY = 0F

        override fun onScale(detector: ScaleGestureDetector): Boolean =//TODO
                true

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            focusX = detector.focusX
            focusY = detector.focusY
            return super.onScaleBegin(detector)
        }

    }

    init {
        setImageResource(R.drawable.batuframe)
        scaleType = ScaleType.MATRIX
        scaleGastureDetector = ScaleGestureDetector(context, simpleOnScaleGestureListener)
    }

    fun setImage(resource: Int){
        setImageResource(resource)
    }

}