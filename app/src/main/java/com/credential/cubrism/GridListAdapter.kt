package com.credential.cubrism

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class GridListAdapter(private val items: ArrayList<String>) : RecyclerView.Adapter<GridListAdapter.ViewHolder>() {
    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val listName = v.findViewById<TextView>(R.id.listName)
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
        holder.listName.text = items[position]
        holder.listName.setOnClickListener {
            Toast.makeText(holder.listName.context, "${items[position]} clicked!", Toast.LENGTH_SHORT).show()
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