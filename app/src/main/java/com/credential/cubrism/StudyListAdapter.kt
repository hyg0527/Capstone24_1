package com.credential.cubrism

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

interface StudyItemClickListener {
    fun onItemClicked(item: String)
}

class StudyListAdapter(private val items: ArrayList<String>) : RecyclerView.Adapter<StudyListAdapter.ViewHolder>() {
    private var itemClickListener: StudyItemClickListener? = null
    fun setItemClickListener(listener: StudyItemClickListener) {
        this.itemClickListener = listener
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val title = v.findViewById<TextView>(R.id.txtStudyTitle)

        init {
            v.setOnClickListener {
                val position = adapterPosition
                val item = items[position]
                itemClickListener?.onItemClicked(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_list_study, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = items[position]
    }
}