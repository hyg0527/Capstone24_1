package com.credential.cubrism

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Tags(val tag: String? = null, var isEnabled: Boolean = false)

class TagAdapter(private val items: ArrayList<Tags>) : RecyclerView.Adapter<TagAdapter.TapViewHolder>() {
    inner class TapViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tag = v.findViewById<TextView>(R.id.txtTag)

        init {
            v.setOnClickListener {
                val position = adapterPosition
                val item = items[position]

                if (!item.isEnabled) {
                    item.isEnabled = true
                    tag.setBackgroundResource(R.drawable.tag_rounded_corner_selected)
                } else {
                    item.isEnabled = false
                    tag.setBackgroundResource(R.drawable.tag_rounded_corner)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TapViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_list_tag, parent, false)

        return TapViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: TapViewHolder, position: Int) {
        holder.tag.text = items[position].tag
    }

}