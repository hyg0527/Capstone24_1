package com.credential.cubrism

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// ItemClickListener 인터페이스 정의
interface ItemClickListener {
    fun onItemClick(item: GridItems)
}
data class GridItems(val name: String? = null, val icon: Int? = null)

class GridListAdapter(private val items: ArrayList<GridItems>) : RecyclerView.Adapter<GridListAdapter.ViewHolder>() {
    private var itemClickListener: ItemClickListener? = null    // ItemClickListener를 저장할 변수
    fun setItemClickListener(listener: ItemClickListener) {    // ItemClickListener를 설정하는 메서드
        itemClickListener = listener
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val listName = v.findViewById<TextView>(R.id.listName)
        val icon = v.findViewById<ImageView>(R.id.icon)
        init {
            icon.setOnClickListener {
                val position = adapterPosition
                val clickedItem = items[position]
                itemClickListener?.onItemClick(clickedItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_list_medal, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.listName.text = items[position].name

        val currentItem = items[position]
        currentItem.icon?.let {
            holder.icon.setImageResource(it)
        }
    }
}

class ItemSpaceMargin(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.left = space
        outRect.right = space
        outRect.top = space
        outRect.bottom = space
    }
}