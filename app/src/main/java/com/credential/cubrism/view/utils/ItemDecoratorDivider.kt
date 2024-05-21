package com.credential.cubrism.view.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemDecoratorDivider(
    context: Context,
    divTop: Int,
    divBottom: Int,
    divLeft: Int,
    divRight: Int,
    dividerHeight: Int,
    dividerMargin: Int,
    dividerColor: Int?
) : RecyclerView.ItemDecoration() {
    private val divTopDp = dpToPx(context, divTop)
    private val divBottomDp = dpToPx(context, divBottom)
    private val divLeftDp = dpToPx(context, divLeft)
    private val divRightDp = dpToPx(context, divRight)
    private val dividerHeightDp = dpToPx(context, dividerHeight)
    private val dividerMarginDp = dpToPx(context, dividerMargin)

    private val paint = Paint()

    init {
        dividerColor?.let {  paint.color = it }
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = divTopDp
        outRect.bottom = divBottomDp
        outRect.left = divLeftDp
        outRect.right = divRightDp
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft + dividerMarginDp
        val right = parent.width - parent.paddingRight - dividerMarginDp

        val childCount = parent.childCount
        for (i in 0 until childCount - 1) { // 마지막 아이템에는 구분선을 그리지 않음
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + dividerHeightDp

            canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
        }
    }
}