package com.credential.cubrism

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// InfoClickListener 인터페이스 정의
interface InfoClickListener {
    fun onInfoClick(item: String)
}

class GridCategoryAdapter(private val items: ArrayList<String>) : RecyclerView.Adapter<GridCategoryAdapter.CategoryViewHolder>() {
    private var infoClickListener: InfoClickListener? = null
    fun setInfoClickListener(listener: InfoClickListener) {
        infoClickListener = listener
    }
    inner class CategoryViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val categoryName = v.findViewById<TextView>(R.id.txtCategoryName)
        init {
            v.setOnClickListener {
                val position = adapterPosition
                val clickedInfo = items[position]
                infoClickListener?.onInfoClick(clickedInfo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_category_medal, parent, false)
        return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.categoryName.text = items[position]
    }
}