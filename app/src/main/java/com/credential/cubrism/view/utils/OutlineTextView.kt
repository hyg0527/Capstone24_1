package com.credential.cubrism.view.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.credential.cubrism.R

class OutlineTextView : AppCompatTextView {
    private var strokeWidth = 0.0f
    private var strokeColor = 0

    constructor(context: Context?) : super(context!!)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        val type = context.obtainStyledAttributes(attrs, R.styleable.OutlineTextView)
        strokeWidth = type.getFloat(R.styleable.OutlineTextView_outlineWidth, 0.0f)
        strokeColor = type.getColor(R.styleable.OutlineTextView_outlineColor, -0x1)

        type.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        val states = textColors

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        setTextColor(strokeColor)
        super.onDraw(canvas)

        paint.style = Paint.Style.FILL
        setTextColor(states)
        super.onDraw(canvas)
    }
}