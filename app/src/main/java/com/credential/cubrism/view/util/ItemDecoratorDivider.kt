package com.credential.cubrism.view.util

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemDecoratorDivider(
    private val divTop: Int,
    private val divBottom: Int,
    private val divLeft: Int,
    private val divRight: Int,
    private val dividerHeight: Int,
    private val dividerMargin: Int,
    dividerColor: Int?
) : RecyclerView.ItemDecoration() {

    private val paint = Paint()

    init {
        dividerColor?.let {  paint.color = it }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = divTop
        outRect.bottom = divBottom
        outRect.left = divLeft
        outRect.right = divRight
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft + dividerMargin
        val right = parent.width - parent.paddingRight - dividerMargin

        val childCount = parent.childCount
        for (i in 0 until childCount - 1) { // 마지막 아이템에는 구분선을 그리지 않음
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + dividerHeight

            canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
        }
    }
}